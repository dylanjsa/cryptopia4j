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
package com.dylanjsa.cryptopia;

import com.dylanjsa.cryptopia.remote.data.MarketOrders;
import com.dylanjsa.cryptopia.remote.data.TradeSubmission;
import com.dylanjsa.cryptopia.remote.data.enums.TradeType;
import com.dylanjsa.cryptopia.remote.data.enums.TransactionType;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * Really just serves as a sample for using this lib.
 * Created by Dylan Janeke on 2017/06/23.
 */
public class BasicTest {
    final CryptopiaClient client =
            new CryptopiaClient();

    // * -- Public API -- * //

    @Test
    public void testGetCurrencies() {
        System.out.println("* -- GetCurrencies -- *");
        client.getCurrencies().forEach(System.out::println);
    }

    @Test
    public void testGetTradePairs() {
        System.out.println("* -- GetTradePairs -- *");
        client.getTradePairs().forEach(System.out::println);
    }

    @Test
    public void testGetMarkets() {
        System.out.println("* -- GetMarkets -- *");
        client.getMarkets("BTC").forEach(System.out::println);
    }

    @Test
    public void testGetMarket() {
        System.out.println("* -- GetMarket -- *");
        System.out.println(client.getMarket("DOT_BTC"));
    }

    @Test
    public void testGetMarketHistory() {
        System.out.println("* -- GetMarketHistory -- *");
        client.getMarketHistory("DOT_BTC", 1).forEach(System.out::println);
    }

    @Test
    public void testGetMarketOrders() {
        System.out.println("* -- GetMarketOrders -- *");
        MarketOrders orders = client.getMarketOrders("DOT_BTC", 1L);
        orders.getBuy().forEach(System.out::println);
        orders.getSell().forEach(System.out::println);
    }

    @Test
    public void testGetMarketOrderGroups() {
        System.out.println("* -- GetMarketOrderGroups -- *");
        client.getMarketOrderGroups(Arrays.asList("DOT_BTC", "ETH_BTC"), 1).forEach(System.out::println);
    }

    // * -- Private API -- * //

    public void testGetBalance() {
        client.getBalance("DOT_BTC");
    }

    public void testGetDepositAddress() {
        client.getDepositAddress("DOT_BTC");
    }

    public void testGetOpenOrders() {
        client.getOpenOrders("DOT_BTC");
    }

    public void testGetTradeHistory() {
        client.getTradeHistory("DOT_BTC");
    }

    public void testGetTransactions() {
        client.getTransactions(TransactionType.DEPOSIT);
    }

    public void testSubmitTrade() {
        client.submitTrade(new TradeSubmission()
                .setType(TradeType.BUY)
                .setMarket("DOT")
                .setRate(new BigDecimal(0.00000034))
                .setAmount(new BigDecimal(0.00000001))
            );
    }
}