package hello


/**
 * Created by bruno.rocha on 10/31/16.
 */

class WowApp {

    fun start() {
        showGreeting()
        updateAuctionData()
    }

    fun showGreeting() {
        println("---------------------------------")
        println("---------------------------------")
        println("---------------------------------")
        println("---Auction 0.1 by Bruno Rocha----", ANSI_BLUE)
        println("---------------------------------")
        println("---------------------------------")
        println("---------------------------------")
    }

    fun updateAuctionData() {
        println("Retrieving Auction House data", ANSI_YELLOW)
        val api: BattleNetAPI = BattleNetAPI()
        val callResponse = api.getRightUrl()
        val response = callResponse.execute()
        if (response.isSuccessful) {
            val url = response.body().files[0].url
            println("Auction url retrieved. " + url, ANSI_WHITE)
            updatePrices(url) {
                finishUpdate(it)
            }
        } else {
            println("Failed to retrieve auction data, aborting", ANSI_RED)
            finishUpdate(0)
        }
    }

    fun updatePrices(url: String, completion: (Int) -> Unit) {
        if (url == wowApiLink) {
            print("Same as before. Skipping")
            return
        }
        wowApiLink = url
        var baseURL = url.split("auctions.json")[0]
        println("Base auction data url parsed from response: " + baseURL, ANSI_WHITE)
        println("Retrieving auctions", ANSI_YELLOW)
        val api = WoWApi()
        val callResponse = api.getAllAuctions(baseURL)
        val response = callResponse.execute()
        if (response.isSuccessful) {
            val auctions = response.body().auctions
            checkIfLowerPrice(auctions)
            completion(1)
        } else {
            println("Failed to retrieve auction data, aborting", ANSI_RED)
            completion(0)
        }
    }

    fun checkIfLowerPrice(auctions: List<Auctions>) {
        println("Auctions retrieved. Checking lower prices", ANSI_YELLOW)
        resetPrices()
        val starLightRoseId: Long = 124105
        val dreamLeafId: Long = 124102
        val foxFlowerId: Long = 124103
        val fjarnId: Long = 124104
        auctions.forEach {
            if (it.item == starLightRoseId && it.unitPrice() < starlightRosePrice) {
                starlightRosePrice = it.unitPrice()
            }
            if (it.item == dreamLeafId && it.unitPrice() < dreamLeafPrice) {
                dreamLeafPrice = it.unitPrice()
            }
            if (it.item == foxFlowerId && it.unitPrice() < foxFlowerPrice) {
                foxFlowerPrice = it.unitPrice()
            }
            if (it.item == fjarnId && it.unitPrice() < fjarnPrice) {
                fjarnPrice = it.unitPrice()
            }
        }
    }

    fun resetPrices() {
        starlightRosePrice = defaultPrice
        dreamLeafPrice = defaultPrice
        foxFlowerPrice = defaultPrice
        fjarnPrice = defaultPrice
    }

    fun finishUpdate(result: Int) {
        if (result == 1) {
            println("Auction data updated.", ANSI_GREEN)
            println("Waiting 30 minutes...", ANSI_BLUE)
            Thread.sleep(1000 * 60 * 30)
        } else {
            println("Failed to update auction data.", ANSI_RED)
        }
        updateAuctionData()
    }
}