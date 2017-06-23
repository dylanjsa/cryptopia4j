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

import java.math.BigDecimal;

/**
 *
 * Created by Dylan on 2017/06/23.
 */
public class Balance {
    private Long currencyId;
    private String symbol;
    private BigDecimal total;
    private BigDecimal available;
    private BigDecimal unconfirmed;
    private BigDecimal heldForTrades;
    private BigDecimal pendingWithdraw;
    private String address;
    private String baseAddress;
    private String status;
    private String statusMessage;

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getAvailable() {
        return available;
    }

    public void setAvailable(BigDecimal available) {
        this.available = available;
    }

    public BigDecimal getUnconfirmed() {
        return unconfirmed;
    }

    public void setUnconfirmed(BigDecimal unconfirmed) {
        this.unconfirmed = unconfirmed;
    }

    public BigDecimal getHeldForTrades() {
        return heldForTrades;
    }

    public void setHeldForTrades(BigDecimal heldForTrades) {
        this.heldForTrades = heldForTrades;
    }

    public BigDecimal getPendingWithdraw() {
        return pendingWithdraw;
    }

    public void setPendingWithdraw(BigDecimal pendingWithdraw) {
        this.pendingWithdraw = pendingWithdraw;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBaseAddress() {
        return baseAddress;
    }

    public void setBaseAddress(String baseAddress) {
        this.baseAddress = baseAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Balance{");
        sb.append("currencyId=").append(currencyId);
        sb.append(", symbol='").append(symbol).append('\'');
        sb.append(", total=").append(total);
        sb.append(", available=").append(available);
        sb.append(", unconfirmed=").append(unconfirmed);
        sb.append(", heldForTrades=").append(heldForTrades);
        sb.append(", pendingWithdraw=").append(pendingWithdraw);
        sb.append(", address='").append(address).append('\'');
        sb.append(", baseAddress='").append(baseAddress).append('\'');
        sb.append(", status='").append(status).append('\'');
        sb.append(", statusMessage='").append(statusMessage).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
