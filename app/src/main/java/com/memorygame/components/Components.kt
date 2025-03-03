package com.memorygame.components
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.memorygame.main.MemoryGameViewModel
import com.mycomposablelibrary.MyLibraryButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


// =================================================================================================


@Composable
fun TitleText(modifier: Modifier = Modifier) {
    Text(
        text        = "Memory Game",
        color       = Color.Yellow,
        fontSize    = 30.sp,
        textAlign   = TextAlign.Center,
        style       = TextStyle(
                                fontFamily = MaterialTheme.typography.titleMedium.fontFamily,
                                shadow = Shadow(
                                                color = Color.Black,
                                                blurRadius = 6f // Adjust for thickness
                                                )
                                ),
        modifier    = modifier.then(Modifier.padding(top=50.dp))
    )
}

// =================================================================================================

@Composable
fun MatchCountText(text: String) {
    Text(
        text        = text,
        color       = Color.Black,
        style       = MaterialTheme.typography.titleMedium,
        textAlign   = TextAlign.Center
    )
}

// =================================================================================================

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ButtonGrid(viewModel: MemoryGameViewModel) {

    val rows    = 4
    val columns = 6

    val timerCompleted  :MutableState<Boolean>              = remember {mutableStateOf(true)}
    var currentJob      :Job?                               by remember { mutableStateOf(null) }
    val scope           :CoroutineScope                     = rememberCoroutineScope()

    val characterList   :MutableState<Array<Char> >         = remember { mutableStateOf(viewModel.getCharacterArray(rows, columns)) }
    val matchCount      :MutableIntState                    = remember { mutableIntStateOf(0) }
    val matchPair       :MutableList<Int>                   = remember { mutableListOf() }
    val matches         :MutableList<Int>                   = remember { mutableListOf() }
    val infoText        :MutableState<String>               = remember { mutableStateOf("")}
    val buttonStates    :List<Pair<Int, MyButtonState>>     = remember {
                                                                List(rows * columns) { index -> index to MyButtonState() }
                                                            }
    // ---------------------------

    fun reset() {
        characterList.value     = viewModel.getCharacterArray(rows, columns)
        infoText.value          = ""
        matchPair.clear()
        matchCount.intValue     = 0
        matches.clear()
        timerCompleted.value    = true

        buttonStates.forEach { (index, state) ->
            if(state.isVisible) {
                state.toggleVisibility()
                state.toggleColor()
            }
        }
    }

    // ---------------------------

    fun checkForMatch() {

        // If there is a match, increment the match counter and leave the buttons visible
        if(buttonStates[matchPair[0]].second.buttonCharacter == buttonStates[matchPair[1]].second.buttonCharacter) {
            infoText.value = "Match found!"
            matches.add(matchPair[0])
            matches.add(matchPair[1])
            matchCount.intValue++
            matchPair.clear()

        }
        else {
            // Flip the buttons visibility back to unseen if there isn't a match
            infoText.value = "No match"
            buttonStates[matchPair[0]].second.toggleVisibility()
            buttonStates[matchPair[0]].second.toggleColor()
            buttonStates[matchPair[1]].second.toggleVisibility()
            buttonStates[matchPair[1]].second.toggleColor()
            matchPair.clear()
        }
    }

    // ---------------------------

    fun startTimer() {
        currentJob?.cancel()
        currentJob = scope.launch {
            while(!timerCompleted.value) {
                delay(1000) // 1 seconds delay
                checkForMatch()
                delay(1000)
                timerCompleted.value = true // Update state after timer finishes
            }
        }
    }

    // ----------------------------

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(30.dp, Alignment.CenterVertically),
        modifier            = Modifier
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(10.dp)
                                .fillMaxSize()
    ) {
        if(!timerCompleted.value) {
            Text(text = infoText.value)
        }
        else {
            Text(text = "")
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(30.dp, Alignment.CenterVertically)
        ) {
            MatchCountText(matchCount.intValue.toString())
            FlowRow(
                modifier                = Modifier.padding(4.dp),
                horizontalArrangement   = Arrangement.spacedBy(6.dp),
                verticalArrangement     = Arrangement.spacedBy(6.dp),
                maxItemsInEachRow       = rows
            ) {
                buttonStates.forEach { (index, state) ->

                    state.buttonCharacter = characterList.value[index].toString()

                    MyButton(
                        state = state,
                        onClick = {
                            if(index !in matches) {
                                if(!state.isVisible &&  matchPair.size < 2 ) {

                                    matchPair.add(index)
                                    state.toggleColor()
                                    state.toggleVisibility()

                                    if (matchPair.size == 2 ) {
                                        infoText.value = ""
                                        timerCompleted.value = false
                                        startTimer()
                                    }
                                }
                            }
                        },
                        text = state.buttonCharacter
                    )
                }
            }
        }
        MyLibraryButton(onClick = { reset() }, text="Reset", contentPadding = PaddingValues(15.dp))
    }
}

// =================================================================================================

class MyButtonState: ViewModel() {

    var buttonColor     :Color      by mutableStateOf(Color.Blue)
    var isVisible       :Boolean    by mutableStateOf(false)
    var buttonCharacter :String     by mutableStateOf("")

    // --------------------

    fun toggleColor() {
        buttonColor = if (buttonColor == Color.Blue) Color.Green else Color.Blue
    }

    // --------------------

    fun toggleVisibility() {
        isVisible = !isVisible
    }
}

@Composable
fun MyButton(state          : MyButtonState    = remember { MyButtonState() },
             onClick        : () -> Unit,
             text           : String,
             fontSize       : TextUnit         = 20.sp,
             modifier       : Modifier         = Modifier,
             contentPadding : PaddingValues    = PaddingValues(0.dp)) {

    Button(
        onClick         = { onClick() },
        modifier        = modifier,
        contentPadding  = contentPadding,
        shape           = RectangleShape,
        colors          = ButtonDefaults.buttonColors(
                                containerColor  = state.buttonColor,
                                contentColor    = Color.White
                            ),
    ) {
        if(state.isVisible) {
            Text(text       = text,
                fontSize    = fontSize,
                textAlign   = TextAlign.Center)
        }
    }
}

// =================================================================================================

