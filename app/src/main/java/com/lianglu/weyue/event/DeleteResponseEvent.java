package com.lianglu.weyue.event;


import com.lianglu.weyue.db.entity.CollBookBean;

/**
 * Created by LiangLu on 17-12-27.
 */

public class DeleteResponseEvent {
    public boolean isDelete;
    public CollBookBean collBook;
    public DeleteResponseEvent(boolean isDelete, CollBookBean collBook){
        this.isDelete = isDelete;
        this.collBook = collBook;
    }
}
