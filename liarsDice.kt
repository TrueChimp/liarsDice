open class Player() {

    open var name = "Default"
    var dice: Int = 5
    var diceResults = arrayOf<Int>(0, 0, 0, 0, 0)
    var stillPlaying: Boolean = true

    fun decrementDice() {
        this.dice--
    }

    /*open fun calculateRisk(botGame: Boolean, diceTotal: Int) :Double{
        return 0
    }*/

    open fun reevaluateRisk(){}

    open fun call(guess:Array<Int>, total:Int):Boolean{return false}

    open fun takeTurn(diceTotal: Int, pastGuess: Array<Int>) :Array<Int>{
        println("Default turn, you shouldn't be able to see this")
        return arrayOf<Int>(0,0)
    }

    fun rollDice(){
        //Rolls new dice

        //Resets dice
        for(i in 0..4){
            diceResults[i] = 0
        }

        //Rolls new dice
        for(i in 0 until dice) {
            diceResults[i] = randomNumberGenerator(1, 6)
        }
    }
}

class Bot(private val _name: String = "Default") : Player(){

    override var name: String = _name
    val riskAversion: Int = randomNumberGenerator(40,80)
    var risk = 0

    override fun reevaluateRisk(){
        this.risk = riskAversion - ((5 - dice) * 6)
    }

    override fun takeTurn(diceTotal: Int, pastGuess: Array<Int>) :Array<Int>{
        return generateGuess(pastGuess,diceTotal)
    }

    /*override*/ fun calculateRisk(diceTotal: Int, value: Int, amount: Int): Int {

        var knownDice = 0
        for(item in diceResults){
            if(item == value){
                knownDice++
            }
        }

        val doubleKnownDice = knownDice.toDouble()
        val doubleAllDice = diceTotal.toDouble()
        val doubleDice = dice.toDouble()
        val doubleAmount = amount.toDouble()
        val doubleImportantDice = doubleAllDice - doubleDice
        val normalDiceChance:Double = (1.0/6.0)

        val diceProb:Double = (((normalDiceChance)*(doubleImportantDice)) + doubleKnownDice)
        val totalProb:Double = diceProb/doubleAmount
        val percent = totalProb * 100
        val finalPercent = percent.toInt()
        return(finalPercent)
    }

    private fun generateGuess(pastGuess: Array<Int>, total:Int) :Array<Int>{

        var increaseValueRisk = 0
        if ((pastGuess[0] + 1) <= 6){
            increaseValueRisk = calculateRisk(total, pastGuess[0] + 1, pastGuess[1])
        }
        val increaseAmountRisk = calculateRisk(total, pastGuess[0], pastGuess[1] + 1)

        if (increaseValueRisk >= increaseAmountRisk){
            return arrayOf<Int>(pastGuess[0] + 1, pastGuess[1])
        }else if(increaseAmountRisk > increaseValueRisk){
            return arrayOf<Int>(pastGuess[0], pastGuess[1] + 1)
        }

        return arrayOf<Int>(0,0)
    }

    override fun call(guess:Array<Int>, total:Int): Boolean{
        val chanceCorrect = calculateRisk(total,guess[0],guess[1])
        if (chanceCorrect < risk){
            return true
        }
        return false
    }

}

class User(private val _name: String = "Default") : Player(){

    override var name: String = _name

    override fun takeTurn(diceTotal: Int, pastGuess: Array<Int>) :Array<Int>{
        displayDice()
        println("\tPress 1 to make your guess")
        println("\tPress 2 to calculate the chance of the guess being correct")

        var turnNotComplete = true
        var guess = arrayOf<Int>(0,0)

        do{
            print("> ")
            val userSelect = readLine()!!

            when(userSelect.toInt()){
                1 -> {guess = getGuess(pastGuess)
                turnNotComplete = false}

                2 -> calculateRisk(true, diceTotal)
                else -> {
                    println("Input not recognized")
                }
            }
        }while(turnNotComplete)
        return guess
    }

    fun displayDice(){
        print("Your dice are: ")
        for(item in diceResults){
            if (item != 0) {
                print(item)
                print(" ")
            }
        }
        println()
    }

    private fun getGuess(pastGuess: Array<Int>): Array<Int>{
        val userGuess = arrayOf<Int>(0,0)
        do {
            do {
                var badVal = false
                print("What value are you guessing? ")
                var userSelect = readLine()!!
                userGuess[0] = userSelect.toInt()
                if ((userGuess[0] < 1) or (userGuess[0] > 6)){
                    println("Invalid input")
                    badVal = true
                }
            }while(badVal)

            print("How many ")
            print(userGuess[0])
            print("'s are there? ")
            val userSelect = readLine()!!
            userGuess[1] = userSelect.toInt()

            if ((userGuess[0] > pastGuess [0]) or (userGuess[1] > pastGuess[1])){
                return userGuess
            }else{
                println("The move is not valid, try again")
            }
        }while(true)
    }

    /*override*/ fun calculateRisk(botGame: Boolean, diceTotal: Int){

        var allDice = 0
        allDice = if (!botGame){
            getNumDice()
        }else{
            diceTotal
        }
        val value = getValueGuess()
        val amount = getAmountGuess(value)

        if (!botGame){
            getDice()
        }

        var knownDice = 0
        for(item in diceResults){
            if(item == value){
                knownDice++
            }
        }

        val doubleKnownDice = knownDice.toDouble()
        val doubleAllDice = allDice.toDouble()
        val doubleDice = dice.toDouble()
        val doubleAmount = amount.toDouble()
        val doubleImportantDice = doubleAllDice - doubleDice
        val normalDiceChance:Double = (1.0/6.0)


        val diceProb:Double = (((normalDiceChance)*(doubleImportantDice)) + doubleKnownDice)
        val totalProb:Double = diceProb/doubleAmount
        val percent = totalProb * 100
        print("Your chance of success is: ")
        println(kotlin.math.round(percent * 100)/100)


    }

    private fun getNumDice():Int{

        println("How many dice are there in the game? ")
        val userDice = readLine()!!
        return userDice.toInt()
    }

    private fun getValueGuess():Int{
        var value = 0
        do {
            var badVal = false
            print("What value are you guessing? ")
            var userSelect = readLine()!!
            value = userSelect.toInt()
            if ((value < 1) or (value > 6)){
                println("Invalid input")
                badVal = true
            }
        }while(badVal)
        return value
    }

    private  fun getAmountGuess(value:Int):Int{
        print("How many ")
        print(value)
        print("'s are there? ")
        val userSelect = readLine()!!
        return userSelect.toInt()
    }

    override fun call(guess:Array<Int>, total:Int):Boolean{
        print("The current guess is: ")
        print(guess[1])
        print(" ")
        print(guess[0])
        println("'s")
        print("Do you want to call it? (y/n) ")
        var userSelect = ""
        var badInput = true
        do {
            userSelect = readLine()!!
            userSelect = userSelect.lowercase()
            if (userSelect == "y"){
                return true
            }else if (userSelect == "n"){
                return false

            }
        }while(badInput)
        return userSelect == "y"
    }

    private fun getDice(){
        print("What dice do you have? (Enter zero for a null die) ")
        for (i in 0..4){
            print("Die ")
            print(i + 1)
            print(": ")
            var userSelect = readLine()!!
            var userDie = userSelect.toInt()

            diceResults[i] = userDie
        }
    }
}

fun playGame(currentPlayer: User){

    val playerOrder = arrayOf(currentPlayer, Bot("James"), Bot("John"), Bot("Jerry"), Bot("Todd"))
    do{

        var currentRound:Boolean = true
        var diceTotal = 0
        var attacker = Player()
        var defender = Player()

        for(person in playerOrder){
            person.rollDice()
            diceTotal += person.dice
            person.reevaluateRisk()
        }

        var currentGuess = arrayOf<Int>(0,0)
        var personIndex = 0

        do{
            for(person in playerOrder){
                if(person.stillPlaying) {
                    currentGuess = person.takeTurn(diceTotal, currentGuess)
                    print(person.name)
                    print(" has guessed: ")
                    print(currentGuess[1])
                    print(" ")
                    print(currentGuess[0])
                    println("'s")
                }

                var tempIndex = personIndex
                var breakForLoop = false
                tempIndex++

                do{
                    if (tempIndex == 5){
                        tempIndex = 0
                    }
                    if (playerOrder[tempIndex].call(currentGuess, diceTotal)){
                        attacker = playerOrder[tempIndex]
                        defender = playerOrder[personIndex]
                        breakForLoop = true
                        print(attacker.name)
                        print(" calls ")
                        print(defender.name)
                        println("'s bluff!")
                        break
                    }else{
                        print(playerOrder[tempIndex].name)
                        println(" does not call")
                        tempIndex++
                    }
                    if (tempIndex == 5){
                        tempIndex = 0
                    }
                }while(tempIndex!=personIndex)

                if(breakForLoop){
                    break
                }else{
                    personIndex++
                }
            }
            currentRound = false

            if (isBluffTrue(playerOrder,currentGuess[0], currentGuess[1])){
                print(attacker.name)
                println(" loses a die!")
                attacker.decrementDice()
                print(attacker.dice)
                if(attacker.dice == 0){
                    attacker.stillPlaying = false
                }
            }else{
                print(defender.name)
                println(" loses a die!")
                defender.decrementDice()
                print(defender.dice)
                if(defender.dice == 0){
                    defender.stillPlaying = false
                }
            }

        }while(currentRound)
    }while(currentPlayer.stillPlaying)
}

fun printIntro(){
    //This function prints the introduction to the program
    println("Welcome to the Liar's Dice Companion app!")
    println("\tType '1' to play a game of Liar's Dice")
    println("\tType '2' to calculate the probability of success of a bluff")
    println("\tType '3' to quit the program\n")
    println("Enjoy!! :)")
}

fun randomNumberGenerator(start: Int, end: Int):Int{
    //This number generator checks whether the parameters are valid then creates a random number
    check(start < end){"Bad random parameter pass"}
    return (start..end).random()
}

fun isBluffTrue(players: Array<Player>, value:Int, amount:Int):Boolean {
    //This function checks if the bluff is true when a player calls the bluff of another
    var totalValue = 0
    for (player in players) {
        print(player.name)
        print(" has: ")
        for (die in player.diceResults) {
            if (die != 0){
                print(die)
                print(" ")
            }

            if (die == value) {
                totalValue++
            }
        }
        println()
    }

    if (totalValue >= amount) {
        return true
    }
    return false
}

fun main(){
    //Starts and holds all other functions
    val currentPlayer = User("User")
    var quitProgram: Boolean = false

    printIntro()

    do{
        print("> ")
        val userSelect = readLine()!!

        when(userSelect.toInt()) {
            1 -> playGame(currentPlayer)
            2 -> currentPlayer.calculateRisk(false,0)
            3 -> quitProgram = true
            else -> {
                println("Input not recognized!")
            }
        }
    }while(!quitProgram)
}