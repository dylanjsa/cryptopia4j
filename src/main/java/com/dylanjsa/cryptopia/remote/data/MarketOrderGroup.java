/*
 *
 *  * The MIT License
 *  *
 *  * Copyright 2017 Dylan Janeke <dylancjaneke@gmail.com>.
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in
 *  * all copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  * THE SOFTWARE.
 *
 */
package com.dylanjsa.cryptopia.remote.data;

import java.util.List;

/**
 * Created by Dylan Janeke on 2017/06/22.
 */
public class MarketOrderGroup {
    private Long tradePairId;
    private String market;
    private List<MarketOrder> buy;
    private List<MarketOrder> sell;

    public Long getTradePairId() {
        return tradePairId;
    }

    public void setTradePairId(Long tradePairId) {
        this.tradePairId = tradePairId;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public List<MarketOrder> getBuy() {
        return buy;
    }

    public void setBuy(List<MarketOrder> buy) {
        this.buy = buy;
    }

    public List<MarketOrder> getSell() {
        return sell;
    }

    public void setSell(List<MarketOrder> sell) {
        this.sell = sell;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MarketOrderGroup{");
        sb.append("tradePairId=").append(tradePairId);
        sb.append(", market='").append(market).append('\'');
        sb.append(", buy=").append(buy);
        sb.append(", sell=").append(sell);
        sb.append('}');
        return sb.toString();
    }
}
