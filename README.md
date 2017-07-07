# cryptopia4j
Simple java client side code for interacting with the Cryptopia API.

## Why
Well, writing JSON parsers sucks - it's tedious work.
I wrote this and figured I'll share it. I hope it helps!

## Status
I wouldn't call this 'production-ready', literally grafted through the majority of methods available public and private and implemented them without really testing.
Create an issue if you find anthing that's a problem.
That being said there are some private methods I didn't implement, these are:
 - CancelTrade
 - SubmitTip
 - SubmitWithdraw
 - SubmitTransfer
 
 Feel free to send me a pull request if you decide to implement them.
 
 ## Maven
 Not yet, for now you can just include the code if you want - or add it to your multi-project gradle build.
 
## Usage
```
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
```

## Dependencies
Nothing but gson and junit.

## Disclaimer
I take no responsibility if you lose any funds by making incorrect trades or bugs encountered in this software.

## Contributions
Some coin is always appreciated, anything will help.

| Ethereum | 0xA4f5fE327e6Ecc65Ee2c8F3BC1f61967C02eBfB5 |
| Aragon | 0xA4f5fE327e6Ecc65Ee2c8F3BC1f61967C02eBfB5 |
| Augur | 0xA4f5fE327e6Ecc65Ee2c8F3BC1f61967C02eBfB5 |
| Bitcoin | 1FByZdg8NZFcn1gRoTqS7CdyF6a8ecEm4K |
| Dash | XwQoJMqXQ8EVRtWQez3dJJB7j3GKuWCneY |
| Decred | Dsg4C1GqNu1Ma8MQTnCFNcsS4b96dBTA3A8 |
| EOS | 0xA4f5fE327e6Ecc65Ee2c8F3BC1f61967C02eBfB5 |
| Golem | 0xA4f5fE327e6Ecc65Ee2c8F3BC1f61967C02eBfB5 |
| Litecoin | LLSRFWGbdqwUAuwgNNt3vjhsRWFw9gfQiK |

For any other coin donations please create an issue and I'll add an address, btw thanks for looking at the 'contributions' section, much appreciated!

## License

cryptopia4j is available under the MIT license. See the LICENSE file for more info.
