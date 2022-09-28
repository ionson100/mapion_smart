package com.example.mapion.orm2;

/********************************************************************
 * Copyright © 2016-2017 OOO Bitnic                                 *
 * Created by OOO Bitnic on 08.02.16   corp@bitnic.ru               *
 * ionson100@gmail.com                                              *
 * ******************************************************************/
public interface IUserType {
    Object getObject(String str);

    String getString(Object ojb);
}
