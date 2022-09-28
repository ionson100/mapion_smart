package com.example.mapion.orm2;
/********************************************************************
 * Copyright © 2016-2017 OOO Bitnic                                 *
 * Created by OOO Bitnic on 08.02.16   corp@bitnic.ru               *
 * ionson100@gmail.com                                              *
 * ******************************************************************/

/**
 * Этот интерфей ипользовать  как замену сурогатного поля по uuid,
 * он в основном работает для вытаскивания одиночной записи по гуиду
 */
public interface IUsingGuidId {
    String get_id();
}
