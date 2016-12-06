package org.ucomplex.ucomplex.NavDrawer;

import android.graphics.Bitmap;

import lombok.Data;

/**
 * Created by Sermilion on 02/11/2016.
 */

@Data
public class DrawerListItem {
    private int id;
    private String profileBitmapCode;
    private String title1;
    private String title2;
    private int icon;

    public DrawerListItem(int icon, String title){
        this.icon = icon;
        this.title1 = title;
    }

    public DrawerListItem(String code, String name, String type, int id){
        this.profileBitmapCode = code;
        this.title1 = name;
        this.title2 = type;
        this.id = id;
    }
}
