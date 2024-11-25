package org.myproject.shortlink.project.service;

import java.net.MalformedURLException;

public interface URLTitleService {
    /*
    * 获取链接标题
    * */
    String getTitle(String url) throws MalformedURLException;
}
