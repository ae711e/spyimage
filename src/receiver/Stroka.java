/*
 * Copyright (c) 2018. Eremin
 * 02.10.18 13:44
 */

/*
  Строка данных выводимая на таблицу
 */

package receiver;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Stroka {
  private final StringProperty     mind;	// индекс сообщения
  private final StringProperty 		datt;	// дата сообщения
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

  public Stroka(String mind, String datt, String from, String fnam)
  {
    //LocalDate d = LocalDate.now();
    this.mind = new SimpleStringProperty(mind);
    this.datt = new SimpleStringProperty(datt);
    this.from = new SimpleStringProperty(from);
    this.fnam = new SimpleStringProperty(fnam);
  }

}

