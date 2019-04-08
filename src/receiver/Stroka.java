/*
 * Copyright (c) 2018. Eremin
 * 02.10.18 13:44
 */

/*
  Строка данных выводимая на таблицу
 */

package receiver;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Stroka {
  private final StringProperty     mind;	// индекс сообщения
  private final StringProperty 		datt;	// дата сообщения
  // private final ObjectProperty<LocalDateTime> dat;

  private final StringProperty     from;	// от кого
  private final StringProperty     fnam;	// имя файла вложения


  public String getMind() {
    return mind.get();
  }
  public StringProperty mindProperty() {
    return mind;
  }
  public void setMind(String mind) {
    this.mind.set(mind);
  }

  public String getDatt() {
    return datt.get();
  }
  public StringProperty dattProperty() {
    return datt;
  }
  public void setDatt(String datt) {
    this.datt.set(datt);
  }


  public String getFrom() {
    return from.get();
  }
  public StringProperty fromProperty() {
    return from;
  }
  public void setFrom(String from) {
    this.from.set(from);
  }

  public String getFnam() {
    return fnam.get();
  }
  public StringProperty fnamProperty() {
    return fnam;
  }
  public void setFnam(String fnam) {
    this.fnam.set(fnam);
  }

//  public LocalDateTime getDat() {
//    return dat.get();
//  }
//
//  public ObjectProperty<LocalDateTime> datProperty() {
//    return dat;
//  }
//
//  public void setDat(LocalDateTime dat) {
//    //this.dat.set(dat);
//    this.dat.setValue(dat);
//  }
//
//  public void setDat(String dat) {
//    if(dat == null) {
//      setDat((LocalDateTime) null);
//    } else {
//      // http://proglang.su/java/regular-expressions
//      if(dat.matches("\\d+-\\d+-\\d+$"))
//        dat = dat + " 00:00:00";
//      LocalDateTime ld = LocalDateTime.parse(dat, fmtldt);
//      this.dat.set(ld);
//    }
//  }

  private final static DateTimeFormatter fmtldt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  public Stroka(String mind, String datt, String from, String fnam)
  {
    //LocalDate d = LocalDate.now();
    this.mind = new SimpleStringProperty(mind);
    this.datt = new SimpleStringProperty(datt);
    this.from = new SimpleStringProperty(from);
    this.fnam = new SimpleStringProperty(fnam);
    //this.dat = new SimpleObjectProperty<>();    setDat(datt);
  }

}

