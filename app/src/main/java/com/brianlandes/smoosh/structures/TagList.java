package com.brianlandes.smoosh.structures;

import com.brianlandes.smoosh.utils.TagUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brian on 8/11/2017.
 */

public class TagList {
    private ArrayList<Tag> list = new ArrayList<Tag>();

    public ArrayList<QuickCallback> changeListeners = new ArrayList<>();

    public void Add( Tag tag ) {
        list.add(tag);
        NotifyChangeListeners();
    }

    public void AddAll( List<Tag> tags ) {
        list.addAll(tags);
        NotifyChangeListeners();
    }

    public void RemoveAllThenAddAll( List<Tag> tags ) {
        list.clear();
        list.addAll(tags);
        NotifyChangeListeners();
    }

    public Tag Get( int position ) {
        return list.get(position);
    }

    public Tag GetLast() {
        return list.get(list.size()-1);
    }

    public int GetPosition( Tag tag ) {
        int i =0;
        for ( Tag current: list ) {
            if ( TagUtils.Equals(tag,current) )
                return i;
            i ++;
        }
        return -1;
    }

    public void Clear() {
        list.clear();
        NotifyChangeListeners();
    }

    public void Remove( Tag tag ) {
        boolean removed = list.remove(tag );
        if ( removed)
            NotifyChangeListeners();
    }

    public int Size() {
        return list.size();
    }

    public void NotifyChangeListeners() {
        for ( QuickCallback callback : changeListeners ) {
            callback.Activate();
        }
    }

    public List<Tag> Tags() {
        List<Tag> l = new ArrayList<Tag>();
        l.addAll(list);
        return l;
    }

}
