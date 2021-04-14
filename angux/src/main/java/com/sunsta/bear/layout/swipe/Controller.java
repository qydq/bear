/*
 * Copyright 2016 sunst
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on ali "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sunsta.bear.layout.swipe;

/**
 * 请关注个人知乎bgwan， 在【an系列】专栏会有本【ali框架】的详细使用案例（20190922-正在持续更新中...）
 * <p>
 * 中文描述：Controller
 * <br/><a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以点击这里关注我获取更详细的信息</a><br/>
 * <h3><a href="https://zhuanlan.zhihu.com/p/80668416">版权声明：(C) 2016 The Android Developer Sunst</a></h3>
 * <br>创建日期（可选）：2019/06/09
 * <br>邮件Email：qyddai@gmail.com
 * <br>Github：<a href ="https://qydq.github.io">qydq</a>
 * <br>知乎主页：<a href="https://zhihu.com/people/qydq">Bgwan</a>
 * @author sunst // sunst0069
 * @version 4.0（可选） |   2019/11/30          |   适配androidx重构
 * @link 知乎主页： https://zhihu.com/people/qydq
 */
interface Controller {

    /**
     * The menu is open?
     *
     * @return true, otherwise false.
     */
    boolean isMenuOpen();

    /**
     * The menu is open on the left?
     *
     * @return true, otherwise false.
     */
    boolean isLeftMenuOpen();

    /**
     * The menu is open on the right?
     *
     * @return true, otherwise false.
     */
    boolean isRightMenuOpen();

    /**
     * The menu is completely open?
     *
     * @return true, otherwise false.
     */
    boolean isCompleteOpen();

    /**
     * The menu is completely open on the left?
     *
     * @return true, otherwise false.
     */
    boolean isLeftCompleteOpen();

    /**
     * The menu is completely open on the right?
     *
     * @return true, otherwise false.
     */
    boolean isRightCompleteOpen();

    /**
     * The menu is open?
     *
     * @return true, otherwise false.
     */
    boolean isMenuOpenNotEqual();

    /**
     * The menu is open on the left?
     *
     * @return true, otherwise false.
     */
    boolean isLeftMenuOpenNotEqual();

    /**
     * The menu is open on the right?
     *
     * @return true, otherwise false.
     */
    boolean isRightMenuOpenNotEqual();

    /**
     * Open the current menu.
     */
    void smoothOpenMenu();

    /**
     * Open the menu on left.
     */
    void smoothOpenLeftMenu();

    /**
     * Open the menu on right.
     */
    void smoothOpenRightMenu();

    /**
     * Open the menu on left for the duration.
     *
     * @param duration duration time.
     */
    void smoothOpenLeftMenu(int duration);

    /**
     * Open the menu on right for the duration.
     *
     * @param duration duration time.
     */
    void smoothOpenRightMenu(int duration);

    // ---------- closeMenu. ---------- //

    /**
     * Smooth closed the menu.
     */
    void smoothCloseMenu();

    /**
     * Smooth closed the menu on the left.
     */
    void smoothCloseLeftMenu();

    /**
     * Smooth closed the menu on the right.
     */
    void smoothCloseRightMenu();

    /**
     * Smooth closed the menu for the duration.
     *
     * @param duration duration time.
     */
    void smoothCloseMenu(int duration);

}