package com.edu.wustbook.Model;

import java.util.List;

public interface IAdapter<T> {

    public abstract List<T> getDatas();

    public abstract T getData(int position);

    public abstract void updateData(int position, T t);

    public abstract void insertData(int position, T t);

    public abstract void insertData(List<T> ts);

    public abstract void deleteDate(int position);

    public abstract void deleteAllDate();
}
