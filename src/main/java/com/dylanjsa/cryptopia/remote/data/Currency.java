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

import com.dylanjsa.cryptopia.remote.data.enums.CurrencyListingStatus;
import com.dylanjsa.cryptopia.remote.data.enums.CurrencyStatus;

import java.math.BigDecimal;

/**
 * Created by Dylan Janeke on 2017/06/22.
 */
public class Currency {
    private Long id;
    private String name;
    private String symbol;
    private String algorithm;
    private BigDecimal withdrawFee;
    private BigDecimal minWithdraw;
    private BigDecimal minBaseTrade;
    private boolean isTipEnabled;
    private BigDecimal minTip;
    private Long depositConfirmations;
    private CurrencyStatus status;
    private String statusMessage;
    private CurrencyListingStatus listingStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public BigDecimal getWithdrawFee() {
        return withdrawFee;
    }

    public void setWithdrawFee(BigDecimal withdrawFee) {
        this.withdrawFee = withdrawFee;
    }

    public BigDecimal getMinWithdraw() {
        return minWithdraw;
    }

    public void setMinWithdraw(BigDecimal minWithdraw) {
        this.minWithdraw = minWithdraw;
    }

    public BigDecimal getMinBaseTrade() {
        return minBaseTrade;
    }

    public void setMinBaseTrade(BigDecimal minBaseTrade) {
        this.minBaseTrade = minBaseTrade;
    }

    public boolean isTipEnabled() {
        return isTipEnabled;
    }

    public void setTipEnabled(boolean tipEnabled) {
        isTipEnabled = tipEnabled;
    }

    public BigDecimal getMinTip() {
        return minTip;
    }

    public void setMinTip(BigDecimal minTip) {
        this.minTip = minTip;
    }

    public Long getDepositConfirmations() {
        return depositConfirmations;
    }

    public void setDepositConfirmations(Long depositConfirmations) {
        this.depositConfirmations = depositConfirmations;
    }

    public CurrencyStatus getStatus() {
        return status;
    }

    public void setStatus(CurrencyStatus status) {
        this.status = status;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public CurrencyListingStatus getListingStatus() {
        return listingStatus;
    }

    public void setListingStatus(CurrencyListingStatus listingStatus) {
        this.listingStatus = listingStatus;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Currency{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", symbol='").append(symbol).append('\'');
        sb.append(", algorithm='").append(algorithm).append('\'');
        sb.append(", withdrawFee=").append(withdrawFee);
        sb.append(", minWithdraw=").append(minWithdraw);
        sb.append(", minBaseTrade=").append(minBaseTrade);
        sb.append(", isTipEnabled=").append(isTipEnabled);
        sb.append(", minTip=").append(minTip);
        sb.append(", depositConfirmations=").append(depositConfirmations);
        sb.append(", status=").append(status);
        sb.append(", statusMessage='").append(statusMessage).append('\'');
        sb.append(", listingStatus=").append(listingStatus);
        sb.append('}');
        return sb.toString();
    }
}
