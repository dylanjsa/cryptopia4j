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

import com.dylanjsa.cryptopia.remote.*;
import com.dylanjsa.cryptopia.remote.data.*;
import com.dylanjsa.cryptopia.remote.data.enums.CurrencyListingStatus;
import com.dylanjsa.cryptopia.remote.data.enums.CurrencyStatus;
import com.dylanjsa.cryptopia.remote.data.enums.TradePairStatus;
import com.dylanjsa.cryptopia.remote.data.enums.TransactionType;
import com.google.gson.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 * Missing methods:
 *  - CancelTrade
 *  - SubmitTip
 *  - SubmitWithdraw
 *  - SubmitTransfer
 *
 * Created by Dylan Janeke on 2017/06/22.
 */
public class CryptopiaClient {
    private static final String ROOT_URL = "https://www.cryptopia.co.nz";
    private static final String PRIVATE_PATH = "api";

    private String key;
    private String secretKey;

    public CryptopiaClient setKey(String key) {
        this.key = key;
        return this;
    }

    public CryptopiaClient setSecretKey(String secretKey) {
        this.secretKey = secretKey;
        return this;
    }

    /**
     * Performs a raw public API query.
     * @param method the REST resource
     * @return result JSON
     */
    private String publicApiQuery(String method) {
        BufferedReader in = null;
        try {
            final String urlMethod = String.format("%s/%s/%s", ROOT_URL, PRIVATE_PATH, method);
            final URLConnection con = new URL(urlMethod).openConnection();
            final HttpsURLConnection httpsConn = (HttpsURLConnection) con;
            httpsConn.setRequestMethod("GET");
            httpsConn.setInstanceFollowRedirects(true);
            con.setRequestProperty("Content-Type", "application/json");
            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            final StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            return response.toString();
        } catch (Exception e) {
            throw new CryptopiaClientException("An error occurred communicating with Cryptopia.", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {}
            }
        }
    }

    /**
     * Performs a raw private API query.
     * @param method the REST resource
     * @param postParam the JSON to send in the request body.
     * @return result JSON
     */
    //Thanks to sampey from https://www.cryptopia.co.nz/Forum/Thread/262
    private String privateApiQuery(String method, String postParam) {
        if (secretKey == null) {
            throw new CryptopiaClientException("Please set your secret key before attempting " +
                    "to access any private API methods.");
        }
        if (key == null) {
            throw new CryptopiaClientException("Please set your key before attempting " +
                    "to access any private API methods.");
        }
        BufferedReader in = null;
        try {
            final String urlMethod = String.format("%s/%s/%s", ROOT_URL, PRIVATE_PATH, method);
            final String nonce = String.valueOf(System.currentTimeMillis());
            final StringBuilder reqSignature = new StringBuilder();
            reqSignature
                    .append(key)
                    .append("POST")
                    .append(URLEncoder.encode(urlMethod, StandardCharsets.UTF_8.toString()).toLowerCase())
                    .append(nonce)
                    .append(getMD5_B64(postParam));
            final StringBuilder auth = new StringBuilder();
            auth.append("amx ")
                    .append(key)
                    .append(":")
                    .append(sha256_B64(reqSignature.toString()))
                    .append(":")
                    .append(nonce);
            final URLConnection con = new URL(urlMethod).openConnection();
            con.setDoOutput(true);
            final HttpsURLConnection httpsConn = (HttpsURLConnection) con;
            httpsConn.setRequestMethod("POST");
            httpsConn.setInstanceFollowRedirects(true);
            con.setRequestProperty("Authorization", auth.toString());
            con.setRequestProperty("Content-Type", "application/json");
            final DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postParam);
            wr.flush();
            wr.close();
            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            return response.toString();
        }catch (Exception e) {
            throw new CryptopiaClientException("An error occurred communicating with Cryptopia.", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {}
            }
        }
    }

    private String getMD5_B64(String postParameter) throws Exception {
        return Base64.getEncoder().encodeToString(
                MessageDigest.getInstance("MD5").digest(postParameter.getBytes("UTF-8")));
    }

    private String sha256_B64(String msg) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(Base64.getDecoder().decode(secretKey), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        return Base64.getEncoder().encodeToString(sha256_HMAC.doFinal(msg.getBytes("UTF-8")));
    }

    /**
     *
     * @return all currency data.
     */
    public List<Currency> getCurrencies() {
        final String methodName = "GetCurrencies";
        final String jsonResponse = publicApiQuery(methodName);
        final ApiResponse<List<Currency>> resp =
                new ApiResponse<>();
        final List<Currency> data = new ArrayList<>();
        resp.setData(data);
        final JsonElement jElement = new JsonParser().parse(jsonResponse);
        final JsonObject rootObject = jElement.getAsJsonObject();
        final JsonArray dataArray = rootObject.get("Data").getAsJsonArray();
        resp.setJson(jsonResponse);
        resp.setMessage(rootObject.get("Message").toString());
        resp.setSuccess(rootObject.get("Success").getAsBoolean());
        validateResponse(resp);
        for (final JsonElement element : dataArray) {
            final Currency result = new Currency();
            data.add(result);
            final JsonObject object = element.getAsJsonObject();
            result.setId(object.get("Id").getAsLong());
            result.setName(object.get("Name").toString());
            result.setSymbol(object.get("Symbol").toString());
            result.setAlgorithm(object.get("Algorithm").toString());
            result.setWithdrawFee(object.get("WithdrawFee").getAsBigDecimal());
            result.setMinWithdraw(object.get("MinWithdraw").getAsBigDecimal());
            result.setMinBaseTrade(object.get("MinBaseTrade").getAsBigDecimal());
            result.setTipEnabled(object.get("IsTipEnabled").getAsBoolean());
            result.setMinTip(object.get("MinTip").getAsBigDecimal());
            result.setDepositConfirmations(object.get("DepositConfirmations").getAsLong());
            result.setStatus(CurrencyStatus.byLabel(object.get("Status").toString().replace("\"","")));
            result.setStatusMessage(object.get("StatusMessage").toString());
            result.setListingStatus(CurrencyListingStatus.byLabel(object.get("ListingStatus").toString().replace("\"","")));
        }
        return data;
    }

    /**
     *
     * @return all Trade Pair data.
     */
    public List<TradePair> getTradePairs() {
        final String methodName = "GetTradePairs";
        final String jsonResponse = publicApiQuery(methodName);
        final ApiResponse<List<TradePair>> resp =
                new ApiResponse<>();
        final List<TradePair> data = new ArrayList<>();
        resp.setData(data);
        final JsonElement jElement = new JsonParser().parse(jsonResponse);
        final JsonObject rootObject = jElement.getAsJsonObject();
        final JsonArray dataArray = rootObject.get("Data").getAsJsonArray();
        resp.setJson(jsonResponse);
        resp.setMessage(rootObject.get("Message").toString());
        resp.setSuccess(rootObject.get("Success").getAsBoolean());
        validateResponse(resp);
        for (final JsonElement element : dataArray) {
            final TradePair result = new TradePair();
            data.add(result);
            final JsonObject object = element.getAsJsonObject();
            result.setId(object.get("Id").getAsLong());
            result.setLabel(object.get("Label").toString());
            result.setCurrency(object.get("Currency").toString());
            result.setSymbol(object.get("Symbol").toString());
            result.setBaseCurrency(object.get("BaseCurrency").toString());
            result.setBaseSymbol(object.get("BaseSymbol").toString());
            result.setStatus(TradePairStatus.byLabel(object.get("Status").toString().replace("\"","")));
            result.setStatusMessage(object.get("StatusMessage").toString());
            result.setTradeFee(object.get("TradeFee").getAsBigDecimal());
            result.setMinimumTrade(object.get("MinimumTrade").getAsBigDecimal());
            result.setMaximumTrade(object.get("MaximumTrade").getAsBigDecimal());
            result.setMinimumBaseTrade(object.get("MinimumBaseTrade").getAsBigDecimal());
            result.setMaximumBaseTrade(object.get("MaximumBaseTrade").getAsBigDecimal());
            result.setMinimumPrice(object.get("MinimumPrice").getAsBigDecimal());
            result.setMaximumPrice(object.get("MaximumPrice").getAsBigDecimal());
        }
        return data;
    }

    /**
     *
     * @return All Market data
     */
    public List<Market> getMarkets() {
        final String methodName = "GetMarkets";
        final String jsonResponse = publicApiQuery(methodName);
        return parseGetMarketsResponse(jsonResponse);
    }

    /**
     *
     * @param baseMarket The market to filter on
     * @return All market data filtered on specified baseMarket
     */
    public List<Market> getMarkets(String baseMarket) {
        final String methodName = String.format("GetMarkets/%s", baseMarket);
        final String jsonResponse = publicApiQuery(methodName);
        return parseGetMarketsResponse(jsonResponse);
    }

    /**
     *
     * @param hours Amount of hours data to fetch
     * @return All market data for the last hours
     */
    public List<Market> getMarkets(Integer hours) {
        final String methodName = String.format("GetMarkets/%d", hours);
        final String jsonResponse = publicApiQuery(methodName);
        return parseGetMarketsResponse(jsonResponse);
    }

    /**
     *
     * @param baseMarket The market to filter on
     * @param hours Amount of hours data to fetch
     * @return All market data filtered on specified baseMarket for the last hours
     */
    public List<Market> getMarkets(String baseMarket, Integer hours) {
        final String methodName = String.format("GetMarkets/%s/%d", baseMarket, hours);
        final String jsonResponse = publicApiQuery(methodName);
        return parseGetMarketsResponse(jsonResponse);
    }

    /**
     *
     * @param tradePair The traid pair to filter on
     * @return market data for the specified trade pair
     */
    public Market getMarket(String tradePair) {
        final String methodName = String.format("GetMarket/%s", tradePair);
        final String jsonResponse = publicApiQuery(methodName);
        return parseGetMarketResponse(jsonResponse);
    }

    /**
     *
     * @param tradePair The trade pair to filter on
     * @param hours The amount of historical data to get in hours
     * @return market data for the specified trade pair for the last hours
     */
    public Market getMarket(String tradePair, Integer hours) {
        final String methodName = String.format("GetMarket/%s/%d", tradePair, hours);
        final String jsonResponse = publicApiQuery(methodName);
        return parseGetMarketResponse(jsonResponse);
    }

    /**
     * @param tradePair The trade pair to filter on.
     * @return the market history data for the specified trade pair.
     */
    public List<MarketHistory> getMarketHistory(String tradePair) {
        final String methodName = String.format("GetMarketHistory/%s", tradePair);
        final String jsonResponse = publicApiQuery(methodName);
        return parseMarketHistoryResponse(jsonResponse);
    }

    /**
     * @param tradePair The trade pair to filter on.
     * @param hours the amount of hours to fetch historical data for
     * @return the market history data for the specified trade pair for the last hours.
     */
    public List<MarketHistory> getMarketHistory(String tradePair, Integer hours) {
        final String methodName = String.format("GetMarketHistory/%s/%d", tradePair, hours);
        final String jsonResponse = publicApiQuery(methodName);
        return parseMarketHistoryResponse(jsonResponse);
    }

    /**
     *
     * @param tradePair the trade pair to filter on
     * @return the open buy and sell orders for the specified trade pair.
     */
    public MarketOrders getMarketOrders(String tradePair) {
        final String methodName = String.format("GetMarketOrders/%s", tradePair);
        final String jsonResponse = publicApiQuery(methodName);
        return parseMarketOrdersResponse(jsonResponse);
    }

    /**
     *
     * @param tradePair the trade pair to filter on
     * @param orderCount the amount of orders to fetch
     * @return the open buy and sell orders for the specified trade pair limited to the number of orders.
     */
    public MarketOrders getMarketOrders(String tradePair, Long orderCount) {
        final String methodName = String.format("GetMarketOrders/%s/%d", tradePair, orderCount);
        final String jsonResponse = publicApiQuery(methodName);
        return parseMarketOrdersResponse(jsonResponse);
    }

    /**
     *
     * @param marketNames names of markets to fetch.
     * @return the open buy and sell orders for the specified markets.
     */
    public List<MarketOrderGroup> getMarketOrderGroups(List<String> marketNames) {
        final String methodName = String.format("GetMarketOrderGroups/%s", String.join("-", marketNames));
        final String jsonResponse = publicApiQuery(methodName);
        return parseMarketOrderGroupsResponse(jsonResponse);
    }
    /**
     *
     * @param marketNames names of markets to fetch.
     * @param orderCount limit the number of orders in the response.
     * @return the open buy and sell orders for the specified markets limited by the number of orders.
     */
    public List<MarketOrderGroup> getMarketOrderGroups(List<String> marketNames, Integer orderCount) {
        final String methodName = String.format("GetMarketOrderGroups/%s/%d", String.join("-", marketNames), orderCount);
        final String jsonResponse = publicApiQuery(methodName);
        return parseMarketOrderGroupsResponse(jsonResponse);
    }

    /**
     *
     * @param currency The currency symbol of the balance to return e.g. 'DOT'.
     * @return all balances for a specific currency
     */
    public List<Balance> getBalance(String currency) {
        final String methodName = "GetBalance";
        final JsonObject jo = new JsonObject();
        jo.addProperty("Currency", currency);
        final String jsonResponse = privateApiQuery(methodName, jo.toString());
        return parseBalances(jsonResponse);
    }

    /**
     *
     * @param currencyId Cryptopia currency identifier of the balance to return e.g. '2'
     * @return all balances for a specific currency
     */
    public List<Balance> getBalance(Long currencyId) {
        final String methodName = "GetBalance";
        final JsonObject jo = new JsonObject();
        jo.addProperty("CurrencyId", currencyId);
        final String jsonResponse = privateApiQuery(methodName, jo.toString());
        return parseBalances(jsonResponse);
    }

    /**
     *
     * @param currency The currency symbol of the balance to return e.g. 'DOT'.
     * @return all balances for a specific currency
     */
    public DepositAddress getDepositAddress(String currency) {
        final String methodName = "GetDepositAddress";
        final JsonObject jo = new JsonObject();
        jo.addProperty("Currency", currency);
        final String jsonResponse = privateApiQuery(methodName, jo.toString());
        return parseDepositAddress(jsonResponse);
    }

    /**
     *
     * @param currencyId Cryptopia currency identifier of the balance to return e.g. '2'
     * @return all balances for a specific currency
     */
    public DepositAddress getDepositAddress(Long currencyId) {
        final String methodName = "GetDepositAddress";
        final JsonObject jo = new JsonObject();
        jo.addProperty("CurrencyId", currencyId);
        final String jsonResponse = privateApiQuery(methodName, jo.toString());
        return parseDepositAddress(jsonResponse);
    }

    /**
     *
     * @param market The market symbol of the orders to return e.g. 'DOT/BTC' (not required if 'TradePairId' supplied)
     * @return a list of open orders for all tradepairs or specified tradepair
     */
    public List<OpenOrder> getOpenOrders(String market) {
        final String methodName = "GetOpenOrders";
        final JsonObject jo = new JsonObject();
        jo.addProperty("Market", market);
        final String jsonResponse = privateApiQuery(methodName, jo.toString());
        return parseOpenOrders(jsonResponse);
    }

    /**
     *
     * @param tradePairId The Cryptopia tradepair identifier of the orders to return e.g. '100' (not required if 'Market' supplied)
     * @return a list of open orders for all tradepairs or specified tradepair
     */
    public List<OpenOrder> getOpenOrders(Long tradePairId) {
        final String methodName = "GetOpenOrders";
        final JsonObject jo = new JsonObject();
        jo.addProperty("TradePairId", tradePairId);
        final String jsonResponse = privateApiQuery(methodName, jo.toString());
        return parseOpenOrders(jsonResponse);
    }

    /**
     *
     * @param market The market symbol of the orders to return e.g. 'DOT/BTC' (not required if 'TradePairId' supplied)
     * @param count The maximum amount of orders to return e.g. '10' (default: 100)
     * @return a list of open orders for all tradepairs or specified tradepair
     */
    public List<OpenOrder> getOpenOrders(String market, Long count) {
        final String methodName = "GetOpenOrders";
        final JsonObject jo = new JsonObject();
        jo.addProperty("Market", market);
        jo.addProperty("Count", count);
        final String jsonResponse = privateApiQuery(methodName, jo.toString());
        return parseOpenOrders(jsonResponse);
    }

    /**
     *
     * @param tradePairId The Cryptopia tradepair identifier of the orders to return e.g. '100' (not required if 'Market' supplied)
     * @param count The maximum amount of orders to return e.g. '10' (default: 100)
     * @return a list of open orders for all tradepairs or specified tradepair
     */
    public List<OpenOrder> getOpenOrders(Long tradePairId, Long count) {
        final String methodName = "GetOpenOrders";
        final JsonObject jo = new JsonObject();
        jo.addProperty("TradePairId", tradePairId);
        jo.addProperty("Count", count);
        final String jsonResponse = privateApiQuery(methodName, jo.toString());
        return parseOpenOrders(jsonResponse);
    }

    /**
     *
     * @param market The market symbol of the history to return e.g. 'DOT/BTC'
     * @return a list of trade history for all tradepairs or specified tradepair
     */
    public List<TradeHistory> getTradeHistory(String market) {
        final String methodName = "GetTradeHistory";
        final JsonObject jo = new JsonObject();
        jo.addProperty("Market", market);
        final String jsonResponse = privateApiQuery(methodName, jo.toString());
        return parseTradeHistory(jsonResponse);
    }

    /**
     *
     * @param tradePairId The Cryptopia tradepair identifier of the history to return e.g. '100'
     * @return a list of trade history for all tradepairs or specified tradepair
     */
    public List<TradeHistory> getTradeHistory(Long tradePairId) {
        final String methodName = "GetTradeHistory";
        final JsonObject jo = new JsonObject();
        jo.addProperty("TradePairId", tradePairId);
        final String jsonResponse = privateApiQuery(methodName, jo.toString());
        return parseTradeHistory(jsonResponse);
    }

    /**
     *
     * @param market The market symbol of the history to return e.g. 'DOT/BTC'
     * @param count The maximum amount of history to return e.g. '10'
     * @return a list of trade history for all tradepairs or specified tradepair
     */
    public List<TradeHistory> getTradeHistory(String market, Long count) {
        final String methodName = "GetTradeHistory";
        final JsonObject jo = new JsonObject();
        jo.addProperty("Market", market);
        jo.addProperty("Count", count);
        final String jsonResponse = privateApiQuery(methodName, jo.toString());
        return parseTradeHistory(jsonResponse);
    }

    /**
     *
     * @param tradePairId The Cryptopia tradepair identifier of the history to return e.g. '100'
     * @param count The maximum amount of history to return e.g. '10'
     * @return a list of trade history for all tradepairs or specified tradepair
     */
    public List<TradeHistory> getTradeHistory(Long tradePairId, Long count) {
        final String methodName = "GetTradeHistory";
        final JsonObject jo = new JsonObject();
        jo.addProperty("TradePairId", tradePairId);
        jo.addProperty("Count", count);
        final String jsonResponse = privateApiQuery(methodName, jo.toString());
        return parseTradeHistory(jsonResponse);
    }

    /**
     *
     * @param type The type of transactions to return e.g. 'Deposit' or 'Withdraw'
     * @return a list of transactions
     */
    public List<Transaction> getTransactions(TransactionType type) {
        final String methodName = "GetTransactions";
        final JsonObject jo = new JsonObject();
        jo.addProperty("Type", type.getLabel());
        final String jsonResponse = privateApiQuery(methodName, jo.toString());
        return parseTransactions(jsonResponse);
    }

    /**
     *
     * @param type The type of transactions to return e.g. 'Deposit' or 'Withdraw'
     * @param count The maximum amount of transactions to return e.g. '10' (default: 100)
     * @return a list of transactions
     */
    public List<Transaction> getTransactions(TransactionType type, Long count) {
        final String methodName = "GetTransactions";
        final JsonObject jo = new JsonObject();
        jo.addProperty("Type", type.getLabel());
        jo.addProperty("Count", count);
        final String jsonResponse = privateApiQuery(methodName, jo.toString());
        return parseTransactions(jsonResponse);
    }

    public SubmittedTrade submitTrade(TradeSubmission submission) {
        final String methodName = "SubmitTrade";
        final JsonObject jo = new JsonObject();
        if (submission.getMarket() != null) {
            jo.addProperty("Market", submission.getMarket());
        }
        if (submission.getMarketId() != null) {
            jo.addProperty("MarketId", submission.getMarketId());
        }
        jo.addProperty("TradeType", submission.getType().getLabel());
        jo.addProperty("Rate", submission.getRate());
        jo.addProperty("Amount", submission.getAmount());
        final String jsonResponse = privateApiQuery(methodName, jo.toString());
        final JsonElement jElement = new JsonParser().parse(jsonResponse);
        final JsonObject rootObject = jElement.getAsJsonObject();
        final ApiResponse<SubmittedTrade> resp = new ApiResponse<>();
        resp.setJson(jsonResponse);
        resp.setSuccess(rootObject.get("Success").getAsBoolean());
        resp.setMessage(rootObject.get("Error").toString());
        validateResponse(resp);
        final JsonObject object = rootObject.get("Data").getAsJsonObject();
        final SubmittedTrade trade = new SubmittedTrade();
        trade.setOrderId(object.get("OrderId").getAsLong());
        trade.setFilledOrders(new ArrayList<>());
        final JsonArray jsonArray = object.get("FilledOrders").getAsJsonArray();
        for (JsonElement arrayElement : jsonArray) {
            final JsonObject jFilledOrder = arrayElement.getAsJsonObject();
            trade.getFilledOrders().add(jFilledOrder.getAsLong());
        }
        return trade;
    }

    private List<Transaction> parseTransactions(String jsonResponse) {
        final ApiResponse<List<Transaction>> resp = new ApiResponse<>();
        final List<Transaction> data = new ArrayList<>();
        resp.setData(data);
        final JsonElement jElement = new JsonParser().parse(jsonResponse);
        final JsonObject rootObject = jElement.getAsJsonObject();
        resp.setJson(jsonResponse);
        resp.setSuccess(rootObject.get("Success").getAsBoolean());
        resp.setMessage(rootObject.get("Error").toString());
        validateResponse(resp);
        final JsonArray jsonArray = rootObject.get("Data").getAsJsonArray();
        for (final JsonElement element : jsonArray) {
            final JsonObject object = element.getAsJsonObject();
            final Transaction item = new Transaction();
            data.add(item);
            item.setId(object.get("Id").getAsLong());
            item.setCurrency(object.get("Currency").toString());
            item.setTxId(object.get("TxId").toString());
            item.setType(TransactionType.byLabel(object.get("Type").toString().replace("\"","")));
            item.setAmount(object.get("Amount").getAsBigDecimal());
            item.setFee(object.get("Fee").getAsBigDecimal());
            item.setStatus(object.get("Status").toString());
            item.setConfirmations(object.get("Confirmations").getAsLong());
            item.setTime(new Date(object.get("TimeStamp").getAsLong()));
            item.setAddress(object.get("Address").toString());
        }
        return data;
    }



    private List<TradeHistory> parseTradeHistory(String jsonResponse) {
        final ApiResponse<List<TradeHistory>> resp = new ApiResponse<>();
        final List<TradeHistory> data = new ArrayList<>();
        resp.setData(data);
        final JsonElement jElement = new JsonParser().parse(jsonResponse);
        final JsonObject rootObject = jElement.getAsJsonObject();
        resp.setJson(jsonResponse);
        resp.setSuccess(rootObject.get("Success").getAsBoolean());
        resp.setMessage(rootObject.get("Error").toString());
        validateResponse(resp);
        final JsonArray jsonArray = rootObject.get("Data").getAsJsonArray();
        for (final JsonElement element : jsonArray) {
            final JsonObject object = element.getAsJsonObject();
            final TradeHistory item = new TradeHistory();
            data.add(item);
            item.setTradeId(object.get("TradeId").getAsLong());
            item.setTradePairId(object.get("TradePairId").getAsLong());
            item.setMarket(object.get("Market").toString());
            item.setType(object.get("Type").toString());
            item.setRate(object.get("Rate").getAsBigDecimal());
            item.setAmount(object.get("Amount").getAsBigDecimal());
            item.setTotal(object.get("Total").getAsBigDecimal());
            item.setFee(object.get("Fee").getAsBigDecimal());
            item.setTime(new Date(object.get("TimeStamp").getAsLong()));
        }
        return data;
    }

    private List<OpenOrder> parseOpenOrders(String jsonResponse) {
        final ApiResponse<List<OpenOrder>> resp = new ApiResponse<>();
        final List<OpenOrder> data = new ArrayList<>();
        resp.setData(data);
        final JsonElement jElement = new JsonParser().parse(jsonResponse);
        final JsonObject rootObject = jElement.getAsJsonObject();
        resp.setJson(jsonResponse);
        resp.setSuccess(rootObject.get("Success").getAsBoolean());
        resp.setMessage(rootObject.get("Error").toString());
        validateResponse(resp);
        final JsonArray jsonArray = rootObject.get("Data").getAsJsonArray();
        for (final JsonElement element : jsonArray) {
            final JsonObject object = element.getAsJsonObject();
            final OpenOrder item = new OpenOrder();
            data.add(item);
            item.setOrderId(object.get("OrderId").getAsLong());
            item.setTradePairId(object.get("TradePairId").getAsLong());
            item.setMarket(object.get("Market").toString());
            item.setType(object.get("Type").toString());
            item.setRate(object.get("Rate").getAsBigDecimal());
            item.setAmount(object.get("Amount").getAsBigDecimal());
            item.setTotal(object.get("Total").getAsBigDecimal());
            item.setRemaining(object.get("Remaining").getAsBigDecimal());
            item.setTime(new Date(object.get("TimeStamp").getAsLong()));
        }
        return data;
    }


    private DepositAddress parseDepositAddress(String jsonResponse) {
        final ApiResponse<DepositAddress> resp =
                new ApiResponse<>();
        final DepositAddress data = new DepositAddress();
        resp.setData(data);
        resp.setData(data);
        final JsonElement jElement = new JsonParser().parse(jsonResponse);
        final JsonObject rootObject = jElement.getAsJsonObject();
        resp.setJson(jsonResponse);
        resp.setSuccess(rootObject.get("Success").getAsBoolean());
        resp.setMessage(rootObject.get("Error").toString());
        validateResponse(resp);
        final JsonObject object = rootObject.get("Data").getAsJsonObject();
        final DepositAddress da = new DepositAddress();
        da.setCurrency(object.get("Currency").toString());
        da.setBaseAddress(object.get("BaseAddress").toString());
        da.setAddress(object.get("Address").toString());
        return da;
    }

    private List<Balance> parseBalances(String jsonResponse) {
        final ApiResponse<List<Balance>> resp = new ApiResponse<>();
        final List<Balance> data = new ArrayList<>();
        resp.setData(data);
        final JsonElement jElement = new JsonParser().parse(jsonResponse);
        final JsonObject rootObject = jElement.getAsJsonObject();
        resp.setJson(jsonResponse);
        resp.setSuccess(rootObject.get("Success").getAsBoolean());
        resp.setMessage(rootObject.get("Error").toString());
        validateResponse(resp);
        final JsonArray jsonArray = rootObject.get("Data").getAsJsonArray();
        for (final JsonElement element : jsonArray) {
            final JsonObject object = element.getAsJsonObject();
            final Balance bal = new Balance();
            data.add(bal);
            bal.setCurrencyId(object.get("CurrencyId").getAsLong());
            bal.setSymbol(object.get("Symbol").toString());
            bal.setTotal(object.get("Total").getAsBigDecimal());
            bal.setAvailable(object.get("Available").getAsBigDecimal());
            bal.setUnconfirmed(object.get("Unconfirmed").getAsBigDecimal());
            bal.setHeldForTrades(object.get("HeldForTrades").getAsBigDecimal());
            bal.setPendingWithdraw(object.get("PendingWithdraw").getAsBigDecimal());
            bal.setAddress(object.get("Address").toString());
            bal.setBaseAddress(object.get("BaseAddress").toString());
            bal.setStatus(object.get("Status").toString());
            bal.setStatusMessage(object.get("StatusMessage").toString());
        }
        return data;
    }

    private List<MarketOrderGroup> parseMarketOrderGroupsResponse(String jsonResponse) {
        final ApiResponse<List<MarketOrderGroup>> resp = new ApiResponse<>();
        final List<MarketOrderGroup> data = new ArrayList<>();
        resp.setData(data);
        final JsonElement jElement = new JsonParser().parse(jsonResponse);
        final JsonObject rootObject = jElement.getAsJsonObject();
        resp.setJson(jsonResponse);
        resp.setMessage(rootObject.get("Message").toString());
        resp.setSuccess(rootObject.get("Success").getAsBoolean());
        validateResponse(resp);
        final JsonArray jsonArray = rootObject.get("Data").getAsJsonArray();
        for (final JsonElement element : jsonArray) {
            final JsonObject object = element.getAsJsonObject();
            final MarketOrderGroup group = new MarketOrderGroup();
            data.add(group);
            group.setTradePairId(object.get("TradePairId").getAsLong());
            group.setMarket(object.get("Market").toString());
            group.setBuy(parseMarketOrders(object.get("Buy").getAsJsonArray()));
            group.setSell(parseMarketOrders(object.get("Sell").getAsJsonArray()));
        }
        return data;
    }

    private MarketOrders parseMarketOrdersResponse(String jsonResponse) {
        final ApiResponse<MarketOrders> resp = new ApiResponse<>();
        final MarketOrders data = new MarketOrders();
        resp.setData(data);
        final JsonElement jElement = new JsonParser().parse(jsonResponse);
        final JsonObject rootObject = jElement.getAsJsonObject();
        resp.setJson(jsonResponse);
        resp.setMessage(rootObject.get("Message").toString());
        resp.setSuccess(rootObject.get("Success").getAsBoolean());
        final JsonObject dataObject = rootObject.get("Data").getAsJsonObject();
        data.setBuy(parseMarketOrders(dataObject.get("Buy").getAsJsonArray()));
        data.setSell(parseMarketOrders(dataObject.get("Sell").getAsJsonArray()));
        validateResponse(resp);
        return data;
    }

    private List<MarketOrder> parseMarketOrders(JsonArray dataArray) {
        final List<MarketOrder> data = new ArrayList<>();
        for (final JsonElement element : dataArray) {
            final MarketOrder result = new MarketOrder();
            data.add(result);
            final JsonObject object = element.getAsJsonObject();
            result.setTradePairId(object.get("TradePairId").getAsLong());
            result.setLabel(object.get("Label").toString());
            result.setPrice(object.get("Price").getAsBigDecimal());
            result.setVolume(object.get("Volume").getAsBigDecimal());
            result.setTotal(object.get("Total").getAsBigDecimal());
        }
        return data;
    }

    private List<MarketHistory> parseMarketHistoryResponse(String jsonResponse) {
        final ApiResponse<List<MarketHistory>> resp = new ApiResponse<>();
        final List<MarketHistory> data = new ArrayList<>();
        resp.setData(data);
        final JsonElement jElement = new JsonParser().parse(jsonResponse);
        final JsonObject rootObject = jElement.getAsJsonObject();
        final JsonArray dataArray = rootObject.get("Data").getAsJsonArray();
        resp.setJson(jsonResponse);
        resp.setMessage(rootObject.get("Message").toString());
        resp.setSuccess(rootObject.get("Success").getAsBoolean());
        validateResponse(resp);
        for (final JsonElement element : dataArray) {
            final MarketHistory result = new MarketHistory();
            data.add(result);
            final JsonObject object = element.getAsJsonObject();
            result.setTradePairId(object.get("TradePairId").getAsLong());
            result.setLabel(object.get("Label").toString());
            result.setType(object.get("Type").toString());
            result.setPrice(object.get("Price").getAsBigDecimal());
            result.setAmount(object.get("Amount").getAsBigDecimal());
            result.setTotal(object.get("Total").getAsBigDecimal());
            result.setTime(new Date(object.get("Timestamp").getAsLong()));
        }
        return data;
    }

    private Market parseGetMarketResponse(String jsonResponse) {
        final ApiResponse<Market> resp = new ApiResponse<>();
        final JsonElement jElement = new JsonParser().parse(jsonResponse);
        final JsonObject rootObject = jElement.getAsJsonObject();
        resp.setJson(jsonResponse);
        resp.setMessage(rootObject.get("Message").toString());
        resp.setSuccess(rootObject.get("Success").getAsBoolean());
        validateResponse(resp);
        final Market market = parseMarketObject(rootObject.get("Data").getAsJsonObject());
        resp.setData(market);
        return market;
    }

    private List<Market> parseGetMarketsResponse(String jsonResponse) {
        final ApiResponse<List<Market>> resp = new ApiResponse<>();
        final List<Market> data = new ArrayList<>();
        resp.setData(data);
        final JsonElement jElement = new JsonParser().parse(jsonResponse);
        final JsonObject rootObject = jElement.getAsJsonObject();
        final JsonArray dataArray = rootObject.get("Data").getAsJsonArray();
        resp.setJson(jsonResponse);
        resp.setMessage(rootObject.get("Message").toString());
        resp.setSuccess(rootObject.get("Success").getAsBoolean());
        for (final JsonElement element : dataArray) {
            final JsonObject object = element.getAsJsonObject();
            final Market result = parseMarketObject(object);
            data.add(result);
        }
        validateResponse(resp);
        return data;
    }

    private Market parseMarketObject(JsonObject object) {
        final Market result = new Market();
        result.setTradePairId(object.get("TradePairId").getAsLong());
        result.setLabel(object.get("Label").toString());
        result.setAskPrice(object.get("AskPrice").getAsBigDecimal());
        result.setBidPrice(object.get("BidPrice").getAsBigDecimal());
        result.setLow(object.get("Low").getAsBigDecimal());
        result.setHigh(object.get("High").getAsBigDecimal());
        result.setVolume(object.get("Volume").getAsBigDecimal());
        result.setLastPrice(object.get("LastPrice").getAsBigDecimal());
        result.setBuyVolume(object.get("BuyVolume").getAsBigDecimal());
        result.setSellVolume(object.get("SellVolume").getAsBigDecimal());
        result.setChange(object.get("Change").getAsBigDecimal());
        result.setOpen(object.get("Open").getAsBigDecimal());
        result.setClose(object.get("Close").getAsBigDecimal());
        result.setBaseVolume(object.get("BaseVolume").getAsBigDecimal());
        result.setBaseBuyVolume(object.get("BuyBaseVolume").getAsBigDecimal());
        result.setBaseSellVolume(object.get("SellBaseVolume").getAsBigDecimal());
        return result;
    }



    /**
     * Checks the response object (as received from cryptopia), and throws a
     * CryptopiaClientException if there was an error.
     *
     * @see CryptopiaClientException
     * @param response the response to check.
     */
    private void validateResponse(ApiResponse response) {
        if (response.isSuccess()) {
            return;
        }
        throw new CryptopiaClientException(response.getMessage());
    }
}