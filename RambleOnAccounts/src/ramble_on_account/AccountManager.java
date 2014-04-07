package ramble_on_account;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 *
 * @author Leixiang
 */
public class AccountManager {

    // THIS IS WHERE WE'LL STORE ONE OF EACH REGION
    private HashMap<String, Account> allAccounts;
    private AccountsImporterExporter accountsImporterExporter;
    private Account currentAccount;
    private Vector<String> accountNames;

    public AccountManager() {
        allAccounts = new HashMap();
        accountsImporterExporter = new AccountsImporterExporter();
        accountNames = new Vector();
    }

    public Collection<Account> getAllAccounts() {
        return allAccounts.values();
    }

    public Account getAccount(String accountName) {
        return allAccounts.get(accountName);
    }

    public Vector<String> getAllAccountNames() {
        return accountNames;
    }

    public int getNumAccounts() {
        return allAccounts.size();
    }

    public Account getCurrentAccount() {
        return currentAccount;
    }

    public boolean hasAccount(String accountName) {
        for (String name : accountNames) {
            if (name.equalsIgnoreCase(accountName)) {
                return true;
            } 
        }
        return false;
    }

    public void addAccount(String accountName, Account accountToAdd) {
        allAccounts.put(accountName, accountToAdd);
        accountNames.add(accountName);
    }

    public void clearAccounts() {
        allAccounts.clear();
    }

    public void removeAccount(String accountName) {
        allAccounts.remove(accountName);
    }

    public void load() {
        accountsImporterExporter.loadAccounts(this);
    }

    public void save() {
        accountsImporterExporter.saveAccounts(this);
    }

    public void setCurrentAccount(Account account) {
        currentAccount = account;
    }
}
