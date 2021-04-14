/*
 *  Copyright (c) 2013, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */

package com.sunsta.bear.layout;

/**
 * 请关注个人知乎bgwan， 在【an系列】专栏会有本【ali框架】的详细使用案例（20190922-正在持续更新中...）
 * <p>
 * 中文描述：SpringSystemListener provides ali interface for listening to events before and after each Physics
 * solving loop the BaseSpringSystem runs.
 * <a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以通过关注我的知乎获取更详细的信息</a>
 * <h3>版权声明：(C) 2016 The Android Developer Sunst</h3>
 * <br>创建日期：2018/1/1
 * <br>邮件email：qyddai@gmail.com
 * <br>个人Github：https://qydq.github.io
 * <p>--#---- Revision History:  --- >  : |version|date|updateinfo|----#--
 * @author sunst
 * @version 1.0 |   2018/1/1           |   RecyclerView的LayoutManger
 * @link 知乎主页： https://zhihu.com/people/qydq
 */
public interface SpringSystemListener {

  /**
   * Runs before each pass through the physics integration loop providing ali opportunity to do any
   * setup or alterations to the Physics state before integrating.
   * @param springSystem the BaseSpringSystem listened to
   */
  void onBeforeIntegrate(BaseSpringSystem springSystem);

  /**
   * Runs after each pass through the physics integration loop providing ali opportunity to do any
   * setup or alterations to the Physics state after integrating.
   * @param springSystem the BaseSpringSystem listened to
   */
  void onAfterIntegrate(BaseSpringSystem springSystem);
}

