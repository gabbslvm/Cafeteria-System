package service;

import database.MenuItemDAO;
import model.MenuItem;
import java.util.List;

public class MenuService {
    private MenuItemDAO menuItemDAO;
    public MenuService(MenuItemDAO menuItemDAO) {
        this.menuItemDAO = menuItemDAO;
    }
    public List<MenuItem> getMenuItems() {
        return menuItemDAO.getMenuItems();
    }
    public boolean addMenuItem(MenuItem item) {
        if (item != null && item.getName() != null && !item.getName().trim().isEmpty() && item.getPrice() >= 0) {
            menuItemDAO.addItem(item); //
            return true;
        }
        System.err.println("Validation failed: Menu item is invalid.");
        return false;
    }

    public boolean updateMenuItem(MenuItem item) {
        if (item != null && item.getId() > 0 && item.getPrice() >= 0) {
            menuItemDAO.updateItem(item); //
            return true;
        }
        System.err.println("Validation failed: Cannot update invalid menu item.");
        return false;
    }
