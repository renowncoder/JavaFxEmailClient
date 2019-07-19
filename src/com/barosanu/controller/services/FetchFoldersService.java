package com.barosanu.controller.services;

import com.barosanu.model.EmailTreeItem;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import java.util.List;

public class FetchFoldersService extends Service<Void> {

    private Store store;
    private EmailTreeItem<String> foldersRoot;
    private List<Folder> folderList;

    public FetchFoldersService(Store store, EmailTreeItem<String> foldersRoot, List<Folder> folderList) {
        this.store = store;
        this.foldersRoot = foldersRoot;
        this.folderList = folderList;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                fetchFolders();
                return null;
            }
        };
    }

    private void fetchFolders() throws MessagingException {
        Folder[] folders = store.getDefaultFolder().list();
        this.handleFolder(folders, foldersRoot);
    }

    private void handleFolder(Folder[] folders, EmailTreeItem parentTree) throws MessagingException {
        for (Folder folder : folders) {
            folderList.add(folder);
            EmailTreeItem<String> emailTreeItem = new EmailTreeItem<String>(folder.getName());
            parentTree.getChildren().add(emailTreeItem);
            emailTreeItem.setExpanded(true);
            fetchMessagesOnFolder(emailTreeItem, folder);
            addMessageListenerToFolder(emailTreeItem, folder);
            Folder[] subFolders = folder.list();
            handleFolder(subFolders, emailTreeItem);
        }
    }

    private void fetchMessagesOnFolder(EmailTreeItem<String> emailFolder, Folder folder) {
        Service fetchMessagesService = new Service() {
            @Override
            protected Task createTask() {
                return new Task() {
                    @Override
                    protected Object call(){
                        try {
                            if (folder.getType() != Folder.HOLDS_FOLDERS) {
                                folder.open(Folder.READ_WRITE);
                                int folderSize = folder.getMessageCount();
                                for (int i = folderSize; i > 0; i--) {
                                    Message currentMessage = folder.getMessage(i);
                                    emailFolder.addEmail(currentMessage);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                };
            }
        };
        fetchMessagesService.start();
    }

    private void addMessageListenerToFolder(EmailTreeItem<String> emailFolder, Folder folder){
        folder.addMessageCountListener(new MessageCountListener() {
            @Override
            public void messagesAdded(MessageCountEvent messageCountEvent) {
                for (int i = 0; i < messageCountEvent.getMessages().length; i++) {
                    try {
                        Message currentMessage = folder.getMessage(folder.getMessageCount() - i);
                        emailFolder.addEmailToTop(currentMessage);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void messagesRemoved(MessageCountEvent messageCountEvent) {
                System.out.println(messageCountEvent.getMessages()[0]);
            }
        });
    }
}
