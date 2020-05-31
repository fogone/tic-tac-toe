package ru.nobirds.minesweeper.fx

import javafx.geometry.Orientation
import javafx.scene.Parent
import javafx.scene.control.SpinnerValueFactory
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import tornadofx.*

class GameConfigurationView() : View() {

    private val model by inject<GameConfigurationModel>()

    override val root: Parent = form {
        buttonbar {
            button("Beginner") {
                action {
                    model.setValues(9, 9, 10)
                }
                style {
                    backgroundColor =
                        multi(Color.GREEN)
                    textFill = Color.WHITE
                }
            }
            button("Intermediate") {
                action {
                    model.setValues(16, 16, 40)
                }
                style {
                    backgroundColor =
                        multi(Color.BLUE)
                    textFill = Color.WHITE
                }
            }
            button("Expert") {
                action {
                    model.setValues(16, 31, 99)
                }
                style {
                    backgroundColor =
                        multi(Color.RED)
                    textFill = Color.WHITE
                }
            }

            style {
                padding = box(horizontal = 5.px, vertical = 10.px)
            }
        }

        fieldset("Configuration") {
            field("Width") {
                slider(7, 50, 9, Orientation.HORIZONTAL) {
                    hgrow = Priority.ALWAYS
                    valueProperty().bindBidirectional(model.widthProperty)
                    isShowTickLabels = true
                    isShowTickMarks = true
                }
                label(model.widthProperty)
            }
            field("Height") {
                slider(7, 50, 9, Orientation.HORIZONTAL) {
                    hgrow = Priority.ALWAYS
                    valueProperty().bindBidirectional(model.heightProperty)
                    isShowTickLabels = true
                    isShowTickMarks = true
                }
                label(model.heightProperty)
            }
            field("Mines number") {
                spinner(true, model.minesNumberProperty, true) {
                    hgrow = Priority.ALWAYS
                    valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(
                        7,
                        1000,
                        10
                    ) as SpinnerValueFactory<Number>
                }
            }
        }

        buttonbar {
            button("Back to game") {
                disableProperty().bind(model.startedProperty.not())
                action {
                    model.backToGame(this@GameConfigurationView)
                }
            }
            button("Start") {
                action {
                    model.startNewGame(this@GameConfigurationView)
                }
            }
        }
    }
}
