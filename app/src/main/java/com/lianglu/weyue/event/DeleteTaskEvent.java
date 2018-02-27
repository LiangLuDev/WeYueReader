package com.lianglu.weyue.event;


import com.lianglu.weyue.db.entity.CollBookBean;

/**
 * Created by LiangLu on 17-12-27.
 */

public class DeleteTaskEvent {
    public CollBookBean collBook;

    public DeleteTaskEvent(CollBookBean collBook) {
        this.collBook = collBook;
    }
}
