package com.rc.hover.hoverx;

import android.os.Handler;

/**
 * Created by jae91 on 2015-10-19.
 */
public class ThreadSpeaker {
    public String text_to_write = null;
    public String text_from_read = null;
    final Handler updateConversationHandler = new Handler();
}
