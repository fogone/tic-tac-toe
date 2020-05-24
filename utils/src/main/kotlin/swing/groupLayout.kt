package ru.nobirds.swing

import java.awt.Component
import java.awt.Container
import java.awt.GridBagConstraints
import javax.swing.GroupLayout


fun Container.cell(component: Component, constraints: GridBagConstraints.() -> Unit) {
    add(component, constraints(constraints))
}

fun constraints(builder: GridBagConstraints.() -> Unit): GridBagConstraints {
    return GridBagConstraints().apply(builder)
}

open class GroupBuilder<G : GroupLayout.Group>(private val layout: GroupLayout, private val group: G) {

    data class ComponentConstraints(val min: Int, val pref: Int, val max: Int)

    fun sequential(builder: SequentialGroupBuilder.() -> Unit) {
        group.addGroup(SequentialGroupBuilder(layout).apply(builder).build())
    }

    fun parallel(alignment: GroupLayout.Alignment = GroupLayout.Alignment.LEADING,
                 builder: ParallelGroupBuilder.() -> Unit) {
        group.addGroup(ParallelGroupBuilder(alignment, layout).apply(builder).build())
    }

    fun component(component: Component, constraints: ComponentConstraints? = null) {
        if(constraints != null) group.addComponent(component, constraints.min, constraints.pref, constraints.max)
        else group.addComponent(component)
    }

    fun customize(builder: G.() -> Unit) {
        group.apply(builder)
    }

    fun build(): G = group

}

class ParallelGroupBuilder(alignment: GroupLayout.Alignment = GroupLayout.Alignment.LEADING,
                           layout: GroupLayout) : GroupBuilder<GroupLayout.ParallelGroup>(layout, layout.createParallelGroup(alignment)) {

    fun component(alignment: GroupLayout.Alignment, component: Component, constraints: ComponentConstraints? = null) = customize {
        if(constraints != null) addComponent(component, alignment, constraints.min, constraints.pref, constraints.max)
        else addComponent(component, alignment)
    }

}

fun ParallelGroupBuilder.baseline(component: Component, constraints: GroupBuilder.ComponentConstraints? = null) {
    component(GroupLayout.Alignment.BASELINE, component, constraints)
}

fun ParallelGroupBuilder.center(component: Component, constraints: GroupBuilder.ComponentConstraints? = null) {
    component(GroupLayout.Alignment.CENTER, component, constraints)
}

fun ParallelGroupBuilder.trailing(component: Component, constraints: GroupBuilder.ComponentConstraints? = null) {
    component(GroupLayout.Alignment.TRAILING, component, constraints)
}

class SequentialGroupBuilder(layout: GroupLayout) : GroupBuilder<GroupLayout.SequentialGroup>(layout, layout.createSequentialGroup())

fun GroupLayout.sequentialHorizontal(builder: SequentialGroupBuilder.() -> Unit) {
    setHorizontalGroup(SequentialGroupBuilder(this).apply(builder).build())
}

fun GroupLayout.parallelHorizontal(alignment: GroupLayout.Alignment = GroupLayout.Alignment.LEADING,
                                   builder: ParallelGroupBuilder.() -> Unit) {
    setHorizontalGroup(ParallelGroupBuilder(alignment, this).apply(builder).build())
}

fun GroupLayout.sequentialVertical(builder: SequentialGroupBuilder.() -> Unit) {
    setVerticalGroup(SequentialGroupBuilder(this).apply(builder).build())
}

fun GroupLayout.parallelVertical(alignment: GroupLayout.Alignment = GroupLayout.Alignment.LEADING,
                                 builder: ParallelGroupBuilder.() -> Unit) {
    setVerticalGroup(ParallelGroupBuilder(alignment, this).apply(builder).build())
}
