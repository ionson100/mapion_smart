package com.example.mapion.orm2;

/********************************************************************
 * Copyright © 2016-2017 OOO Bitnic                                 *
 * Created by OOO Bitnic on 08.02.16   corp@bitnic.ru               *
 * ionson100@gmail.com                                              *
 * ******************************************************************/

public interface IActionOrm<T> {
    void actionBeforeUpdate(T t);

    void actionAfterUpdate(T t);

    void actionBeforeInsert(T t);

    void actionAfterInsert(T t);

    void actionBeforeDelete(T t);

    void actionAfterDelete(T t);
}
