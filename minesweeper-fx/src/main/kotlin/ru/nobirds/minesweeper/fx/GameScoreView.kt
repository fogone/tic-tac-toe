package ru.nobirds.minesweeper.fx

import javafx.scene.Parent
import tornadofx.*

class GameScoreView() : View() {

    private val model by inject<GameScoreModel>()

    override val root: Parent = vbox {
        tableview(model.score) {
            column("Name", ScoreRecord::nameProperty)
            column("Time", ScoreRecord::timeProperty)
            column("Square", ScoreRecord::squareProperty)
            column("Mines", ScoreRecord::minesProperty)
        }

        buttonbar {
            button("Show all") {
                action {
                    model.cellsNumber = 0
                    model.minesNumber = 0
                }
            }
            button("Okay") {
                action {
                    replaceWith<GameConfigurationView>(sizeToScene = true, transition = ViewTransition.Fade(
                        0.4.seconds
                    )
                    )
                }
            }
        }
    }
}
