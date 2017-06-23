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

import com.dylanjsa.cryptopia.remote.data.enums.TradeType;

import java.math.BigDecimal;

/**
 *
 * Created by Dylan Janeke on 2017/06/23.
 */
public class TradeSubmission {
    private String market;
    private Long marketId;
    private TradeType type;
    private BigDecimal rate;
    private BigDecimal amount;

    public String getMarket() {
        return market;
    }

    public TradeSubmission setMarket(String market) {
        this.market = market;
        return this;
    }

    public Long getMarketId() {
        return marketId;
    }

    public TradeSubmission setMarketId(Long marketId) {
        this.marketId = marketId;
        return this;
    }

    public TradeType getType() {
        return type;
    }

    public TradeSubmission setType(TradeType type) {
        this.type = type;
        return this;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public TradeSubmission setRate(BigDecimal rate) {
        this.rate = rate;
        return this;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TradeSubmission setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TradeSubmission{");
        sb.append("market='").append(market).append('\'');
        sb.append(", marketId=").append(marketId);
        sb.append(", type=").append(type);
        sb.append(", rate=").append(rate);
        sb.append(", amount=").append(amount);
        sb.append('}');
        return sb.toString();
    }
}
