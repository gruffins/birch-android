package com.gruffins.birch.app

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.gruffins.birch.Birch
import com.gruffins.birch.Level
import kotlin.concurrent.thread

class MainActivity: Activity() {

    lateinit var toggleDebugButton: Button
    lateinit var toggleLevelButton: Button
    lateinit var toggleConsoleButton: Button
    lateinit var toggleRemoteButton: Button
    lateinit var toggleSynchronousButton: Button
    lateinit var syncConfigurationButton: Button
    lateinit var traceButton: Button
    lateinit var debugButton: Button
    lateinit var infoButton: Button
    lateinit var warnButton: Button
    lateinit var errorButton: Button
    lateinit var stressTestButton: Button

    var isStressTesting = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toggleDebugButton = findViewById(R.id.toggle_debug)
        toggleLevelButton = findViewById(R.id.toggle_level)
        toggleConsoleButton = findViewById(R.id.toggle_console)
        toggleRemoteButton = findViewById(R.id.toggle_remote)
        toggleSynchronousButton = findViewById(R.id.toggle_synchronous)
        syncConfigurationButton = findViewById(R.id.sync_configuration)
        traceButton = findViewById(R.id.trace)
        debugButton = findViewById(R.id.debug)
        infoButton = findViewById(R.id.info)
        warnButton = findViewById(R.id.warn)
        errorButton = findViewById(R.id.error)
        stressTestButton = findViewById(R.id.stress_test)

        toggleDebugButton.setOnClickListener(this::toggleDebug)
        toggleLevelButton.setOnClickListener(this::toggleLevel)
        toggleConsoleButton.setOnClickListener(this::toggleConsole)
        toggleRemoteButton.setOnClickListener(this::toggleRemote)
        toggleSynchronousButton.setOnClickListener(this::toggleSynchronous)
        syncConfigurationButton.setOnClickListener(this::syncConfiguration)
        traceButton.setOnClickListener(this::trace)
        debugButton.setOnClickListener(this::debug)
        infoButton.setOnClickListener(this::info)
        warnButton.setOnClickListener(this::warn)
        errorButton.setOnClickListener(this::error)
        stressTestButton.setOnClickListener(this::stressTest)

        setState()
    }

    private fun syncConfiguration(_view: View) {
        Birch.syncConfiguration()
    }

    private fun toggleDebug(_view: View) {
        Birch.debug = !Birch.debug
        setState()
    }

    private fun toggleLevel(_view: View) {
        Birch.level = when (Birch.level) {
            Level.TRACE -> Level.DEBUG
            Level.DEBUG -> Level.INFO
            Level.INFO -> Level.WARN
            Level.WARN -> Level.ERROR
            Level.ERROR -> null
            else -> Level.TRACE
        }
        setState()
    }

    private fun toggleConsole(_view: View) {
        Birch.console = !Birch.console
        setState()
    }

    private fun toggleRemote(_view: View) {
        Birch.remote = !Birch.remote
        setState()
    }

    private fun toggleSynchronous(_view: View) {
        Birch.synchronous = !Birch.synchronous
        setState()
    }

    private fun trace(_view: View) {
        Birch.t { """
            Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Turpis cursus in hac habitasse platea dictumst quisque sagittis. Cursus risus at ultrices mi tempus imperdiet nulla. Facilisis volutpat est velit egestas dui id ornare. Elit pellentesque habitant morbi tristique senectus et netus et malesuada. Vehicula ipsum a arcu cursus vitae congue mauris rhoncus aenean. Nisl nisi scelerisque eu ultrices vitae auctor eu. Mattis nunc sed blandit libero volutpat sed cras ornare arcu. Enim neque volutpat ac tincidunt. Auctor eu augue ut lectus arcu bibendum at varius vel. In pellentesque massa placerat duis. Ut sem viverra aliquet eget sit amet tellus cras adipiscing. Eget nunc lobortis mattis aliquam faucibus purus in massa tempor.

Integer feugiat scelerisque varius morbi enim. In tellus integer feugiat scelerisque varius. Pellentesque id nibh tortor id aliquet. Elit duis tristique sollicitudin nibh. Ultrices dui sapien eget mi proin sed libero enim sed. Tellus pellentesque eu tincidunt tortor aliquam nulla facilisi cras fermentum. Sem integer vitae justo eget magna fermentum. Metus vulputate eu scelerisque felis imperdiet proin fermentum leo. Habitasse platea dictumst quisque sagittis. Et tortor consequat id porta nibh venenatis cras.

Vel facilisis volutpat est velit egestas dui. Ullamcorper velit sed ullamcorper morbi tincidunt ornare. Adipiscing enim eu turpis egestas pretium aenean pharetra. Arcu non odio euismod lacinia at. In egestas erat imperdiet sed euismod nisi porta. In eu mi bibendum neque egestas. Tempor commodo ullamcorper a lacus vestibulum sed arcu. Tristique senectus et netus et malesuada. Eu non diam phasellus vestibulum lorem sed risus. Risus feugiat in ante metus dictum at tempor commodo ullamcorper. In tellus integer feugiat scelerisque. Mattis pellentesque id nibh tortor id aliquet lectus proin nibh. Mi proin sed libero enim sed faucibus turpis in. A cras semper auctor neque.

Purus gravida quis blandit turpis cursus in hac. Vestibulum rhoncus est pellentesque elit ullamcorper. Eros in cursus turpis massa tincidunt. Arcu cursus euismod quis viverra nibh cras pulvinar mattis. Nunc congue nisi vitae suscipit tellus mauris a. Eleifend mi in nulla posuere sollicitudin aliquam ultrices sagittis orci. Urna nunc id cursus metus aliquam eleifend mi. Ullamcorper dignissim cras tincidunt lobortis. A cras semper auctor neque vitae tempus. Morbi enim nunc faucibus a pellentesque sit amet porttitor eget. At varius vel pharetra vel turpis nunc eget. Integer quis auctor elit sed vulputate. Quam elementum pulvinar etiam non quam. Nisl rhoncus mattis rhoncus urna neque viverra justo nec. Laoreet id donec ultrices tincidunt arcu non sodales neque.

Tristique et egestas quis ipsum suspendisse ultrices gravida. In massa tempor nec feugiat nisl pretium fusce. Risus viverra adipiscing at in tellus. Id diam maecenas ultricies mi eget mauris pharetra. In hac habitasse platea dictumst quisque sagittis. Laoreet id donec ultrices tincidunt arcu. Porttitor rhoncus dolor purus non enim. Dignissim sodales ut eu sem integer vitae. Tincidunt id aliquet risus feugiat in ante metus dictum at. Nunc faucibus a pellentesque sit amet porttitor eget dolor morbi. Turpis egestas integer eget aliquet. Purus ut faucibus pulvinar elementum integer. Vel pretium lectus quam id leo in vitae turpis massa. Est velit egestas dui id ornare arcu odio ut. Viverra maecenas accumsan lacus vel facilisis volutpat est velit. Bibendum at varius vel pharetra. Lacus sed viverra tellus in hac habitasse platea dictumst vestibulum. Pellentesque massa placerat duis ultricies lacus sed. Erat imperdiet sed euismod nisi.

Laoreet sit amet cursus sit amet dictum sit amet justo. In fermentum posuere urna nec tincidunt praesent semper. Massa massa ultricies mi quis hendrerit dolor. Sed faucibus turpis in eu. In tellus integer feugiat scelerisque varius morbi. Sapien pellentesque habitant morbi tristique senectus. Posuere morbi leo urna molestie at elementum. Mattis pellentesque id nibh tortor. Dolor purus non enim praesent elementum. Tincidunt arcu non sodales neque sodales ut etiam. Risus commodo viverra maecenas accumsan lacus vel. Bibendum ut tristique et egestas quis ipsum suspendisse ultrices gravida. Amet dictum sit amet justo donec enim diam vulputate ut. Turpis massa sed elementum tempus egestas sed sed risus. Suspendisse interdum consectetur libero id. Senectus et netus et malesuada fames ac turpis. Aliquam sem fringilla ut morbi tincidunt. Nisi lacus sed viverra tellus in hac habitasse platea.

Tristique senectus et netus et malesuada fames ac. Tortor at auctor urna nunc id cursus metus aliquam eleifend. Faucibus ornare suspendisse sed nisi lacus sed viverra tellus. Id semper risus in hendrerit gravida rutrum quisque non. Neque aliquam vestibulum morbi blandit cursus. Pellentesque habitant morbi tristique senectus. Dui faucibus in ornare quam viverra orci sagittis eu. Pharetra diam sit amet nisl suscipit adipiscing bibendum est. Lectus urna duis convallis convallis. Pulvinar elementum integer enim neque volutpat. Est sit amet facilisis magna etiam. Scelerisque viverra mauris in aliquam sem fringilla ut. Tellus id interdum velit laoreet id donec ultrices tincidunt. Faucibus vitae aliquet nec ullamcorper. Gravida arcu ac tortor dignissim convallis aenean et. Pulvinar etiam non quam lacus suspendisse faucibus interdum posuere. Facilisi morbi tempus iaculis urna. In pellentesque massa placerat duis ultricies lacus sed turpis tincidunt. Diam volutpat commodo sed egestas egestas fringilla phasellus faucibus scelerisque. In massa tempor nec feugiat nisl pretium fusce.

Sed id semper risus in hendrerit gravida rutrum quisque non. Imperdiet dui accumsan sit amet. Ut sem viverra aliquet eget sit amet. Amet facilisis magna etiam tempor orci. Pulvinar elementum integer enim neque volutpat ac tincidunt. Urna nec tincidunt praesent semper feugiat nibh sed. Dignissim sodales ut eu sem. Enim praesent elementum facilisis leo vel fringilla est. Tempus urna et pharetra pharetra. Elit ut aliquam purus sit amet luctus venenatis lectus magna. Vestibulum lectus mauris ultrices eros in. Eget gravida cum sociis natoque penatibus et magnis. Et ligula ullamcorper malesuada proin libero nunc consequat interdum varius. Scelerisque in dictum non consectetur a erat nam at. Turpis in eu mi bibendum neque. Nulla facilisi nullam vehicula ipsum a arcu cursus vitae congue.

Purus in massa tempor nec feugiat. Sit amet luctus venenatis lectus. Ac felis donec et odio pellentesque diam volutpat. Malesuada fames ac turpis egestas sed. Volutpat odio facilisis mauris sit amet massa vitae. Mauris in aliquam sem fringilla ut morbi tincidunt augue. Ultricies tristique nulla aliquet enim tortor at. Auctor elit sed vulputate mi sit amet mauris commodo quis. Sed elementum tempus egestas sed. Lacinia at quis risus sed vulputate odio. Tellus pellentesque eu tincidunt tortor aliquam nulla facilisi cras fermentum.

Penatibus et magnis dis parturient montes. Integer enim neque volutpat ac tincidunt vitae semper quis lectus. Scelerisque purus semper eget duis. Arcu felis bibendum ut tristique. Dui faucibus in ornare quam viverra. Diam donec adipiscing tristique risus nec feugiat in fermentum. Dignissim suspendisse in est ante in nibh mauris. Sed felis eget velit aliquet sagittis id consectetur purus. Sollicitudin tempor id eu nisl nunc mi ipsum faucibus vitae. Integer feugiat scelerisque varius morbi enim nunc faucibus.

Tellus orci ac auctor augue. Enim blandit volutpat maecenas volutpat blandit aliquam etiam erat velit. Bibendum at varius vel pharetra. Diam ut venenatis tellus in metus vulputate eu scelerisque felis. Sagittis id consectetur purus ut faucibus. Odio ut sem nulla pharetra diam sit amet. Risus viverra adipiscing at in tellus integer feugiat. Sapien nec sagittis aliquam malesuada bibendum arcu vitae elementum. Gravida arcu ac tortor dignissim convallis aenean. Duis ultricies lacus sed turpis tincidunt id aliquet risus.

Rhoncus aenean vel elit scelerisque mauris pellentesque pulvinar pellentesque. Sit amet porttitor eget dolor morbi non arcu risus. Vitae suscipit tellus mauris a diam maecenas. Velit scelerisque in dictum non consectetur. Tellus rutrum tellus pellentesque eu tincidunt. Ut pharetra sit amet aliquam id. Tellus integer feugiat scelerisque varius morbi enim. Ut tortor pretium viverra suspendisse. Lectus sit amet est placerat in egestas erat imperdiet sed. A scelerisque purus semper eget duis. Dictum at tempor commodo ullamcorper a lacus vestibulum sed. Nec feugiat nisl pretium fusce id. Eget mi proin sed libero enim sed.

Ac tincidunt vitae semper quis lectus. Quis hendrerit dolor magna eget est lorem ipsum dolor. Nisi scelerisque eu ultrices vitae auctor eu augue. Ullamcorper eget nulla facilisi etiam dignissim diam quis. Integer vitae justo eget magna fermentum iaculis eu non diam. Massa eget egestas purus viverra accumsan in nisl. Fames ac turpis egestas integer eget aliquet nibh praesent. Maecenas volutpat blandit aliquam etiam erat velit scelerisque in. Dui ut ornare lectus sit amet est placerat in. Enim nunc faucibus a pellentesque sit amet porttitor eget dolor. Montes nascetur ridiculus mus mauris vitae ultricies leo integer malesuada. At erat pellentesque adipiscing commodo elit at. Tortor id aliquet lectus proin nibh nisl condimentum id. Risus feugiat in ante metus dictum at. Massa enim nec dui nunc mattis enim ut tellus elementum. Cras pulvinar mattis nunc sed blandit libero volutpat. Egestas sed tempus urna et pharetra pharetra massa. Eget mauris pharetra et ultrices neque ornare. Faucibus interdum posuere lorem ipsum dolor.

Ac turpis egestas sed tempus urna et pharetra pharetra massa. Posuere lorem ipsum dolor sit amet consectetur adipiscing elit duis. Morbi tincidunt ornare massa eget egestas purus viverra accumsan. Magna eget est lorem ipsum dolor. Amet commodo nulla facilisi nullam vehicula ipsum. Nunc scelerisque viverra mauris in aliquam sem fringilla ut morbi. Sed turpis tincidunt id aliquet risus feugiat in ante metus. Aliquet sagittis id consectetur purus ut faucibus pulvinar. Eget nunc lobortis mattis aliquam faucibus purus in. Convallis aenean et tortor at risus viverra adipiscing. Libero id faucibus nisl tincidunt. Morbi blandit cursus risus at ultrices mi. Turpis nunc eget lorem dolor sed viverra ipsum. Lobortis mattis aliquam faucibus purus. Viverra vitae congue eu consequat ac felis donec. Et malesuada fames ac turpis. Erat velit scelerisque in dictum non consectetur a erat nam.

Urna id volutpat lacus laoreet non curabitur gravida arcu. Morbi tempus iaculis urna id volutpat lacus laoreet non. Vel pretium lectus quam id leo in vitae. Ipsum faucibus vitae aliquet nec ullamcorper. Sodales ut etiam sit amet nisl purus in. Etiam non quam lacus suspendisse. Aliquet risus feugiat in ante. Vestibulum mattis ullamcorper velit sed ullamcorper morbi tincidunt ornare. Tincidunt praesent semper feugiat nibh sed. Amet mauris commodo quis imperdiet massa tincidunt.

Sem fringilla ut morbi tincidunt augue interdum velit euismod. Sit amet facilisis magna etiam tempor orci eu. Neque viverra justo nec ultrices dui sapien. Nulla posuere sollicitudin aliquam ultrices sagittis. Arcu odio ut sem nulla pharetra diam sit amet. Eu tincidunt tortor aliquam nulla facilisi cras fermentum. Diam phasellus vestibulum lorem sed risus ultricies tristique nulla aliquet. Scelerisque felis imperdiet proin fermentum leo vel orci. Magna fringilla urna porttitor rhoncus dolor purus non. Nisi est sit amet facilisis magna etiam tempor orci. Tellus elementum sagittis vitae et leo duis ut diam.

Sit amet consectetur adipiscing elit ut aliquam purus sit. Ut tortor pretium viverra suspendisse potenti nullam ac tortor. Pellentesque nec nam aliquam sem et tortor. Sapien eget mi proin sed libero enim sed faucibus turpis. Scelerisque viverra mauris in aliquam sem fringilla ut morbi tincidunt. Sed enim ut sem viverra aliquet eget sit amet. Nibh tortor id aliquet lectus. Consequat nisl vel pretium lectus quam id. A erat nam at lectus urna. Elementum curabitur vitae nunc sed velit dignissim. Aenean sed adipiscing diam donec adipiscing tristique risus nec. In hac habitasse platea dictumst quisque sagittis purus. In hac habitasse platea dictumst vestibulum rhoncus est. Sed libero enim sed faucibus turpis in eu mi. Sit amet luctus venenatis lectus magna fringilla urna porttitor rhoncus. Amet dictum sit amet justo donec enim. Varius quam quisque id diam.

Magna etiam tempor orci eu lobortis elementum nibh. Sapien eget mi proin sed. Pretium vulputate sapien nec sagittis aliquam malesuada bibendum arcu vitae. Turpis in eu mi bibendum neque egestas congue. Aenean sed adipiscing diam donec adipiscing tristique risus nec feugiat. Enim sed faucibus turpis in eu mi bibendum. Auctor urna nunc id cursus metus aliquam eleifend mi in. Sed velit dignissim sodales ut eu sem integer vitae justo. Quis imperdiet massa tincidunt nunc pulvinar sapien et ligula ullamcorper. Porttitor leo a diam sollicitudin tempor. Enim nec dui nunc mattis. Et netus et malesuada fames ac. Risus nec feugiat in fermentum posuere urna nec. Eu turpis egestas pretium aenean. Consequat nisl vel pretium lectus quam id. Orci phasellus egestas tellus rutrum tellus pellentesque eu. Tincidunt augue interdum velit euismod in pellentesque massa placerat duis. Enim nulla aliquet porttitor lacus luctus accumsan tortor posuere. Eleifend donec pretium vulputate sapien nec sagittis aliquam malesuada bibendum.

Amet tellus cras adipiscing enim eu turpis egestas pretium. Egestas congue quisque egestas diam in arcu. Nunc scelerisque viverra mauris in aliquam sem fringilla ut. Dolor sit amet consectetur adipiscing elit. Purus in mollis nunc sed id semper risus. Risus sed vulputate odio ut enim blandit volutpat maecenas volutpat. Pharetra massa massa ultricies mi quis hendrerit. Scelerisque felis imperdiet proin fermentum leo vel orci porta non. Tellus at urna condimentum mattis pellentesque id nibh tortor id. Egestas egestas fringilla phasellus faucibus scelerisque eleifend donec pretium vulputate. Donec adipiscing tristique risus nec. Condimentum mattis pellentesque id nibh tortor id. Aenean euismod elementum nisi quis. Eget duis at tellus at urna condimentum. Sit amet mauris commodo quis imperdiet.

Vestibulum lorem sed risus ultricies tristique nulla aliquet enim tortor. Lectus urna duis convallis convallis tellus id interdum velit. Iaculis nunc sed augue lacus viverra. Ultrices mi tempus imperdiet nulla malesuada pellentesque. Molestie nunc non blandit massa enim nec dui nunc. Nisl tincidunt eget nullam non nisi est sit. Dui vivamus arcu felis bibendum ut tristique. Turpis in eu mi bibendum neque egestas congue quisque egestas. Pellentesque dignissim enim sit amet venenatis urna cursus eget. Interdum velit laoreet id donec ultrices. Diam volutpat commodo sed egestas egestas fringilla. Amet dictum sit amet justo donec. Cras adipiscing enim eu turpis egestas pretium aenean pharetra. Id consectetur purus ut faucibus.

Arcu vitae elementum curabitur vitae nunc sed velit dignissim. Aliquet enim tortor at auctor urna nunc id cursus. Nunc consequat interdum varius sit amet mattis. Ullamcorper morbi tincidunt ornare massa eget. Tempor id eu nisl nunc mi ipsum faucibus. Auctor eu augue ut lectus arcu bibendum. Sed viverra ipsum nunc aliquet bibendum enim. Nam at lectus urna duis convallis convallis tellus id. Vitae proin sagittis nisl rhoncus mattis. Tortor at auctor urna nunc id. Sed faucibus turpis in eu mi bibendum. Mauris augue neque gravida in fermentum et sollicitudin. Platea dictumst quisque sagittis purus sit. Enim neque volutpat ac tincidunt vitae semper quis. Praesent semper feugiat nibh sed pulvinar proin.

Ipsum a arcu cursus vitae congue mauris. Duis at tellus at urna condimentum mattis. Nunc consequat interdum varius sit amet mattis vulputate. Eu feugiat pretium nibh ipsum consequat nisl vel pretium lectus. Tincidunt lobortis feugiat vivamus at augue eget arcu dictum. Fermentum dui faucibus in ornare quam viverra orci sagittis. Non arcu risus quis varius quam quisque id diam vel. Aliquet porttitor lacus luctus accumsan tortor posuere ac. Lacus vel facilisis volutpat est velit egestas dui id ornare. Morbi quis commodo odio aenean sed adipiscing diam. Ut morbi tincidunt augue interdum velit euismod in pellentesque massa. Quis eleifend quam adipiscing vitae. Non tellus orci ac auctor augue mauris augue. Dapibus ultrices in iaculis nunc sed.

Augue interdum velit euismod in pellentesque massa placerat duis. Senectus et netus et malesuada fames ac turpis egestas. Convallis tellus id interdum velit laoreet id. Ut sem nulla pharetra diam sit amet nisl suscipit. Viverra accumsan in nisl nisi scelerisque. Bibendum est ultricies integer quis. Tortor vitae purus faucibus ornare suspendisse sed nisi lacus. Turpis egestas pretium aenean pharetra magna ac placerat vestibulum. Sed libero enim sed faucibus turpis in. Lectus quam id leo in. Nibh sit amet commodo nulla facilisi nullam vehicula. Phasellus vestibulum lorem sed risus ultricies tristique nulla aliquet enim.

Magna fermentum iaculis eu non diam phasellus. Nulla pellentesque dignissim enim sit. Et netus et malesuada fames ac. Eget felis eget nunc lobortis mattis aliquam. In dictum non consectetur a erat nam at lectus urna. Quis risus sed vulputate odio ut. Cras tincidunt lobortis feugiat vivamus at augue eget. Quam elementum pulvinar etiam non quam lacus suspendisse faucibus interdum. Sem nulla pharetra diam sit amet nisl. Vitae tortor condimentum lacinia quis vel eros. Phasellus faucibus scelerisque eleifend donec pretium. Donec pretium vulputate sapien nec. Blandit cursus risus at ultrices mi. Mi bibendum neque egestas congue quisque egestas diam in. Tortor posuere ac ut consequat semper viverra nam libero. Eget egestas purus viverra accumsan.

Pretium lectus quam id leo in vitae. Aliquet bibendum enim facilisis gravida. Consequat ac felis donec et odio pellentesque diam volutpat commodo. Dis parturient montes nascetur ridiculus mus mauris vitae ultricies leo. Pulvinar elementum integer enim neque volutpat. Et malesuada fames ac turpis egestas. Urna cursus eget nunc scelerisque viverra. Id aliquet lectus proin nibh nisl. Neque egestas congue quisque egestas diam in. Aliquam ut porttitor leo a diam. Magna fermentum iaculis eu non. Felis imperdiet proin fermentum leo vel. Malesuada proin libero nunc consequat interdum varius sit amet mattis. Iaculis eu non diam phasellus.

Varius quam quisque id diam. Orci phasellus egestas tellus rutrum tellus. Sed odio morbi quis commodo odio aenean sed adipiscing. Dignissim enim sit amet venenatis. Metus dictum at tempor commodo ullamcorper a lacus vestibulum sed. Venenatis lectus magna fringilla urna porttitor. Hac habitasse platea dictumst vestibulum rhoncus est pellentesque elit. Vitae et leo duis ut diam quam nulla porttitor massa. Ultrices dui sapien eget mi proin. Tristique magna sit amet purus gravida. Nisl tincidunt eget nullam non. Velit dignissim sodales ut eu sem integer vitae justo. Pretium aenean pharetra magna ac placerat vestibulum lectus mauris. Semper eget duis at tellus at urna condimentum mattis pellentesque. Nisi est sit amet facilisis. Congue mauris rhoncus aenean vel. Volutpat commodo sed egestas egestas fringilla phasellus faucibus scelerisque eleifend. Eros in cursus turpis massa tincidunt. Et leo duis ut diam quam nulla porttitor massa id. Imperdiet proin fermentum leo vel orci.

Nisl nunc mi ipsum faucibus vitae. Et malesuada fames ac turpis egestas maecenas pharetra convallis posuere. Pretium vulputate sapien nec sagittis aliquam malesuada bibendum. Tellus integer feugiat scelerisque varius morbi enim nunc. In arcu cursus euismod quis viverra nibh cras pulvinar. Mattis molestie a iaculis at erat pellentesque adipiscing. Proin nibh nisl condimentum id venenatis a condimentum. Semper viverra nam libero justo laoreet sit. Egestas egestas fringilla phasellus faucibus. Adipiscing vitae proin sagittis nisl rhoncus mattis rhoncus urna neque. Gravida quis blandit turpis cursus in hac. Sit amet tellus cras adipiscing enim eu. Mi tempus imperdiet nulla malesuada pellentesque elit eget gravida. Egestas congue quisque egestas diam in. Eget mauris pharetra et ultrices neque ornare aenean euismod elementum. Sed augue lacus viverra vitae congue eu consequat ac. Ut sem nulla pharetra diam sit amet nisl suscipit. Scelerisque mauris pellentesque pulvinar pellentesque habitant morbi tristique senectus.

Consectetur a erat nam at lectus urna duis convallis. Ut tellus elementum sagittis vitae et leo duis. Quam quisque id diam vel. Et magnis dis parturient montes nascetur. Porttitor eget dolor morbi non arcu risus. Congue eu consequat ac felis donec. Id venenatis a condimentum vitae sapien pellentesque. Auctor urna nunc id cursus metus aliquam eleifend. Dignissim enim sit amet venenatis. Mauris vitae ultricies leo integer malesuada nunc vel. Proin fermentum leo vel orci porta non pulvinar neque. Sed odio morbi quis commodo odio aenean sed adipiscing. Ornare lectus sit amet est placerat. Ac auctor augue mauris augue neque gravida in fermentum et. Ut aliquam purus sit amet. Egestas integer eget aliquet nibh praesent tristique magna sit amet. Consectetur libero id faucibus nisl tincidunt eget nullam. Mauris cursus mattis molestie a iaculis at erat. Porta non pulvinar neque laoreet suspendisse interdum.

Mollis aliquam ut porttitor leo a diam sollicitudin tempor id. Etiam tempor orci eu lobortis elementum nibh tellus molestie nunc. Amet consectetur adipiscing elit duis tristique sollicitudin. Posuere morbi leo urna molestie at elementum eu facilisis sed. A lacus vestibulum sed arcu non odio euismod. Urna duis convallis convallis tellus id. Parturient montes nascetur ridiculus mus mauris. Id eu nisl nunc mi ipsum. Lorem mollis aliquam ut porttitor. Donec et odio pellentesque diam volutpat commodo sed. Morbi tristique senectus et netus.

Proin nibh nisl condimentum id venenatis a condimentum vitae sapien. Quis imperdiet massa tincidunt nunc pulvinar sapien et. Ornare lectus sit amet est placerat in egestas erat imperdiet. Id nibh tortor id aliquet. Feugiat scelerisque varius morbi enim nunc faucibus a pellentesque. Vel fringilla est ullamcorper eget nulla facilisi etiam dignissim diam. Adipiscing vitae proin sagittis nisl. Tellus cras adipiscing enim eu. Tellus elementum sagittis vitae et leo duis ut diam. Mi tempus imperdiet nulla malesuada pellentesque elit eget. Ac tortor vitae purus faucibus ornare. Leo vel orci porta non pulvinar neque laoreet suspendisse. Velit dignissim sodales ut eu sem. Nisi est sit amet facilisis magna etiam tempor orci. Nunc vel risus commodo viverra. Porttitor massa id neque aliquam vestibulum morbi. Felis eget nunc lobortis mattis aliquam faucibus purus.

Dui nunc mattis enim ut. Ut lectus arcu bibendum at varius vel pharetra. Lacus suspendisse faucibus interdum posuere lorem ipsum dolor sit amet. Nisi scelerisque eu ultrices vitae auctor eu augue ut lectus. Adipiscing elit pellentesque habitant morbi tristique senectus et netus. Eu tincidunt tortor aliquam nulla facilisi cras. Et netus et malesuada fames ac turpis egestas sed tempus. A erat nam at lectus urna duis convallis convallis tellus. Placerat in egestas erat imperdiet sed euismod. Eu lobortis elementum nibh tellus.

Tortor consequat id porta nibh venenatis cras. Amet est placerat in egestas erat imperdiet sed euismod. Diam volutpat commodo sed egestas. Id ornare arcu odio ut sem nulla pharetra diam. Risus pretium quam vulputate dignissim. Leo in vitae turpis massa sed elementum tempus egestas sed. Massa sed elementum tempus egestas sed sed risus. Lobortis scelerisque fermentum dui faucibus in ornare quam. Semper quis lectus nulla at volutpat diam. Nisi porta lorem mollis aliquam. Auctor neque vitae tempus quam pellentesque. Aliquet risus feugiat in ante metus dictum at tempor commodo. Nibh tellus molestie nunc non blandit massa enim. In pellentesque massa placerat duis. In hac habitasse platea dictumst vestibulum.

Volutpat ac tincidunt vitae semper quis lectus. Duis tristique sollicitudin nibh sit amet. Sodales ut etiam sit amet nisl purus in mollis. Auctor neque vitae tempus quam pellentesque nec nam aliquam. Ridiculus mus mauris vitae ultricies leo integer. Est lorem ipsum dolor sit amet consectetur adipiscing. Ut morbi tincidunt augue interdum velit euismod in pellentesque. Neque vitae tempus quam pellentesque. Pellentesque elit eget gravida cum sociis natoque penatibus et magnis. Nisl nunc mi ipsum faucibus. Vitae justo eget magna fermentum iaculis eu non diam phasellus. Erat velit scelerisque in dictum non consectetur. Massa placerat duis ultricies lacus sed. Quis hendrerit dolor magna eget est lorem ipsum.

Montes nascetur ridiculus mus mauris vitae ultricies leo integer. Amet nisl purus in mollis nunc sed id. Dignissim diam quis enim lobortis scelerisque fermentum. Amet dictum sit amet justo donec. Ullamcorper malesuada proin libero nunc consequat interdum. Massa tincidunt nunc pulvinar sapien et ligula. Mi sit amet mauris commodo quis imperdiet massa tincidunt nunc. Aenean pharetra magna ac placerat. Quam vulputate dignissim suspendisse in est. Pellentesque id nibh tortor id aliquet lectus. Adipiscing diam donec adipiscing tristique risus nec feugiat in fermentum. Fermentum dui faucibus in ornare quam viverra. Ac tortor vitae purus faucibus ornare suspendisse sed. Condimentum vitae sapien pellentesque habitant morbi tristique senectus et netus. Sagittis id consectetur purus ut faucibus pulvinar elementum integer enim. Massa tincidunt nunc pulvinar sapien et ligula ullamcorper.

In nisl nisi scelerisque eu ultrices vitae auctor eu. Imperdiet dui accumsan sit amet nulla facilisi morbi tempus iaculis. Morbi tristique senectus et netus et malesuada fames. Ultrices dui sapien eget mi proin sed libero enim. Amet tellus cras adipiscing enim eu turpis egestas pretium aenean. Bibendum arcu vitae elementum curabitur vitae. Amet mattis vulputate enim nulla aliquet porttitor lacus. Dolor sit amet consectetur adipiscing elit duis. Neque convallis a cras semper auctor. Etiam erat velit scelerisque in dictum non consectetur.

Amet nulla facilisi morbi tempus iaculis urna. Sit amet nisl suscipit adipiscing bibendum est. At ultrices mi tempus imperdiet nulla malesuada pellentesque. Massa sapien faucibus et molestie ac feugiat sed. At quis risus sed vulputate odio ut. Commodo odio aenean sed adipiscing diam donec. Imperdiet nulla malesuada pellentesque elit eget gravida. Ipsum suspendisse ultrices gravida dictum fusce ut placerat. Ut etiam sit amet nisl purus in mollis nunc. Justo nec ultrices dui sapien eget mi proin sed libero. Rhoncus est pellentesque elit ullamcorper dignissim cras tincidunt. Metus dictum at tempor commodo ullamcorper a. Ac felis donec et odio pellentesque. Elit duis tristique sollicitudin nibh sit amet. Faucibus vitae aliquet nec ullamcorper sit amet risus nullam eget. Etiam sit amet nisl purus in mollis nunc sed id.

Venenatis a condimentum vitae sapien pellentesque habitant morbi tristique. Egestas dui id ornare arcu odio ut sem. Nam libero justo laoreet sit. Nunc scelerisque viverra mauris in aliquam sem fringilla ut morbi. Sagittis nisl rhoncus mattis rhoncus urna neque. Tellus id interdum velit laoreet id donec ultrices tincidunt arcu. Diam sit amet nisl suscipit adipiscing. Neque viverra justo nec ultrices dui sapien eget mi. Eu feugiat pretium nibh ipsum consequat nisl vel pretium. Urna id volutpat lacus laoreet non. Tristique senectus et netus et malesuada. Orci porta non pulvinar neque laoreet suspendisse interdum consectetur. Massa vitae tortor condimentum lacinia quis vel eros donec ac. Neque volutpat ac tincidunt vitae semper quis lectus. Velit egestas dui id ornare arcu odio ut. Ipsum a arcu cursus vitae congue. Arcu felis bibendum ut tristique et egestas quis ipsum.

Id faucibus nisl tincidunt eget nullam non. Velit aliquet sagittis id consectetur purus ut faucibus. Ut morbi tincidunt augue interdum. Dui nunc mattis enim ut tellus elementum. Auctor elit sed vulputate mi sit amet mauris commodo. Sed sed risus pretium quam vulputate. Eget est lorem ipsum dolor sit amet consectetur. Lobortis elementum nibh tellus molestie nunc non blandit. Morbi tempus iaculis urna id volutpat. Eu augue ut lectus arcu bibendum at. Arcu non sodales neque sodales ut. Sed lectus vestibulum mattis ullamcorper velit sed. Ac turpis egestas integer eget aliquet nibh. Risus quis varius quam quisque id. Commodo elit at imperdiet dui. Mattis pellentesque id nibh tortor id aliquet lectus proin. Amet risus nullam eget felis eget nunc. In hac habitasse platea dictumst quisque.

Imperdiet dui accumsan sit amet. Consequat mauris nunc congue nisi. Morbi tempus iaculis urna id volutpat lacus laoreet non curabitur. Magna eget est lorem ipsum dolor sit amet consectetur. Tincidunt tortor aliquam nulla facilisi cras fermentum odio. Volutpat diam ut venenatis tellus in metus vulputate eu. Facilisis magna etiam tempor orci eu lobortis elementum nibh tellus. Malesuada fames ac turpis egestas maecenas. Viverra mauris in aliquam sem fringilla ut morbi tincidunt augue. Egestas sed tempus urna et pharetra pharetra massa. Felis bibendum ut tristique et egestas. Condimentum vitae sapien pellentesque habitant morbi. Vel fringilla est ullamcorper eget. Malesuada nunc vel risus commodo viverra maecenas accumsan lacus vel. Est sit amet facilisis magna etiam tempor orci eu lobortis. Id donec ultrices tincidunt arcu non sodales neque. Mattis rhoncus urna neque viverra. Auctor neque vitae tempus quam pellentesque nec nam aliquam. Tortor dignissim convallis aenean et tortor at risus. Est ullamcorper eget nulla facilisi etiam dignissim diam quis enim.

Turpis egestas sed tempus urna et pharetra pharetra massa. Orci porta non pulvinar neque laoreet. Felis eget velit aliquet sagittis id consectetur purus ut faucibus. Enim blandit volutpat maecenas volutpat. Consectetur adipiscing elit duis tristique sollicitudin. Ultrices gravida dictum fusce ut. Vulputate dignissim suspendisse in est ante in nibh mauris cursus. Placerat duis ultricies lacus sed turpis tincidunt id aliquet risus. Sed adipiscing diam donec adipiscing tristique risus nec feugiat in. Orci dapibus ultrices in iaculis. At quis risus sed vulputate. Odio euismod lacinia at quis risus. Vulputate dignissim suspendisse in est ante in nibh mauris cursus. Ac tortor vitae purus faucibus ornare suspendisse sed nisi.

Faucibus in ornare quam viverra orci sagittis. Nunc congue nisi vitae suscipit tellus mauris a diam maecenas. Tristique et egestas quis ipsum. Amet facilisis magna etiam tempor orci. Senectus et netus et malesuada fames ac. Id eu nisl nunc mi. Nisl nunc mi ipsum faucibus vitae. Vestibulum morbi blandit cursus risus at. Et molestie ac feugiat sed lectus vestibulum mattis. Aliquam malesuada bibendum arcu vitae elementum curabitur vitae nunc. Senectus et netus et malesuada fames ac turpis egestas. Faucibus interdum posuere lorem ipsum dolor sit amet consectetur. At lectus urna duis convallis convallis tellus id interdum. Pharetra convallis posuere morbi leo urna molestie at elementum eu. In ornare quam viverra orci sagittis eu volutpat odio facilisis. Orci ac auctor augue mauris augue neque gravida in fermentum.

Convallis convallis tellus id interdum velit laoreet id. Gravida quis blandit turpis cursus. Mus mauris vitae ultricies leo integer malesuada. Aliquet sagittis id consectetur purus ut. Mattis enim ut tellus elementum sagittis vitae et. Mi tempus imperdiet nulla malesuada pellentesque elit eget gravida cum. Sagittis vitae et leo duis ut diam quam. Et odio pellentesque diam volutpat commodo. Maecenas accumsan lacus vel facilisis volutpat. Phasellus faucibus scelerisque eleifend donec. Convallis posuere morbi leo urna molestie at. Cursus vitae congue mauris rhoncus aenean vel elit scelerisque. Laoreet sit amet cursus sit. Pretium quam vulputate dignissim suspendisse in est ante. Nunc sed id semper risus in. Posuere lorem ipsum dolor sit amet. Semper risus in hendrerit gravida rutrum quisque non.

Vulputate sapien nec sagittis aliquam malesuada bibendum. Lectus magna fringilla urna porttitor rhoncus dolor purus non enim. Donec ac odio tempor orci. Est sit amet facilisis magna etiam tempor orci. Leo a diam sollicitudin tempor. Ultrices tincidunt arcu non sodales neque sodales ut. Eleifend mi in nulla posuere sollicitudin aliquam ultrices sagittis orci. Pretium vulputate sapien nec sagittis. In hendrerit gravida rutrum quisque non tellus orci ac. Augue eget arcu dictum varius duis at.

Commodo nulla facilisi nullam vehicula ipsum a arcu cursus vitae. Nunc scelerisque viverra mauris in. Tellus in metus vulputate eu scelerisque felis imperdiet proin fermentum. Sit amet est placerat in egestas erat imperdiet sed euismod. Elementum sagittis vitae et leo duis ut diam quam nulla. Euismod elementum nisi quis eleifend quam. Integer malesuada nunc vel risus commodo viverra maecenas. Elit scelerisque mauris pellentesque pulvinar. Nunc sed augue lacus viverra. Sem nulla pharetra diam sit amet nisl suscipit adipiscing bibendum. Elit at imperdiet dui accumsan sit amet nulla facilisi morbi. Consequat mauris nunc congue nisi vitae suscipit tellus mauris a. Egestas pretium aenean pharetra magna. In cursus turpis massa tincidunt dui ut ornare lectus. Nisl tincidunt eget nullam non nisi est sit. Nam at lectus urna duis convallis convallis. Urna molestie at elementum eu facilisis sed odio morbi. Est placerat in egestas erat imperdiet sed euismod nisi porta. Amet facilisis magna etiam tempor orci eu.

Donec adipiscing tristique risus nec feugiat in. In nulla posuere sollicitudin aliquam ultrices sagittis orci a scelerisque. Tincidunt augue interdum velit euismod in pellentesque massa placerat duis. In hendrerit gravida rutrum quisque non. Magna ac placerat vestibulum lectus mauris ultrices. Faucibus interdum posuere lorem ipsum dolor. Nunc mi ipsum faucibus vitae aliquet nec ullamcorper sit. Orci phasellus egestas tellus rutrum tellus pellentesque eu tincidunt tortor. Tellus in metus vulputate eu scelerisque felis. Neque viverra justo nec ultrices dui sapien. Tristique nulla aliquet enim tortor at auctor urna nunc. Diam vulputate ut pharetra sit amet aliquam id diam maecenas. Eleifend donec pretium vulputate sapien nec sagittis aliquam malesuada. Eget nullam non nisi est sit amet facilisis. Ut tellus elementum sagittis vitae et leo duis. Dignissim sodales ut eu sem integer vitae justo. Dolor sit amet consectetur adipiscing elit ut aliquam purus sit. Turpis tincidunt id aliquet risus feugiat in ante metus dictum.

Eu facilisis sed odio morbi quis commodo odio. Placerat vestibulum lectus mauris ultrices eros in cursus turpis. Dolor purus non enim praesent elementum facilisis leo. Praesent elementum facilisis leo vel fringilla est ullamcorper eget. Ultrices vitae auctor eu augue ut lectus. Nulla facilisi cras fermentum odio eu feugiat pretium nibh. Tincidunt arcu non sodales neque sodales ut etiam. Ac ut consequat semper viverra nam libero justo laoreet. Faucibus pulvinar elementum integer enim neque. Nec tincidunt praesent semper feugiat. Gravida neque convallis a cras semper auctor. Duis convallis convallis tellus id interdum velit laoreet id donec. Sed lectus vestibulum mattis ullamcorper velit. Mattis enim ut tellus elementum sagittis. Sed blandit libero volutpat sed cras ornare arcu dui vivamus. Ridiculus mus mauris vitae ultricies. Quam nulla porttitor massa id neque aliquam vestibulum. Posuere urna nec tincidunt praesent. Malesuada pellentesque elit eget gravida cum sociis natoque penatibus et.

Sed velit dignissim sodales ut eu sem integer. Aliquet risus feugiat in ante metus dictum at tempor. Bibendum neque egestas congue quisque egestas diam in. Proin libero nunc consequat interdum varius. Tempus egestas sed sed risus pretium quam vulputate. Sem fringilla ut morbi tincidunt augue interdum velit. Tincidunt arcu non sodales neque. Ipsum suspendisse ultrices gravida dictum fusce ut placerat orci nulla. Vitae tempus quam pellentesque nec. Ullamcorper eget nulla facilisi etiam dignissim diam quis. Ut morbi tincidunt augue interdum velit euismod in pellentesque. Imperdiet nulla malesuada pellentesque elit eget. In aliquam sem fringilla ut morbi. Aliquet nec ullamcorper sit amet. Mi ipsum faucibus vitae aliquet. At risus viverra adipiscing at in tellus. Ac orci phasellus egestas tellus. Arcu risus quis varius quam quisque id diam.

Volutpat blandit aliquam etiam erat velit scelerisque in dictum. Donec ultrices tincidunt arcu non. Accumsan tortor posuere ac ut consequat semper viverra nam. Blandit turpis cursus in hac habitasse. Interdum posuere lorem ipsum dolor. Sed egestas egestas fringilla phasellus faucibus scelerisque eleifend donec. Pulvinar elementum integer enim neque volutpat. A scelerisque purus semper eget duis. Odio facilisis mauris sit amet massa vitae tortor condimentum lacinia. Mauris pharetra et ultrices neque ornare aenean euismod elementum nisi. Ac odio tempor orci dapibus ultrices in iaculis. Nulla facilisi morbi tempus iaculis urna id volutpat lacus. Mi ipsum faucibus vitae aliquet nec ullamcorper sit amet. Eleifend mi in nulla posuere sollicitudin aliquam ultrices sagittis orci.

Congue mauris rhoncus aenean vel elit scelerisque mauris pellentesque pulvinar. Tortor aliquam nulla facilisi cras fermentum odio eu feugiat. Ut pharetra sit amet aliquam id diam maecenas. Arcu dui vivamus arcu felis bibendum ut. Platea dictumst vestibulum rhoncus est pellentesque elit ullamcorper dignissim cras. Risus sed vulputate odio ut enim. Orci ac auctor augue mauris augue neque gravida in. Volutpat diam ut venenatis tellus in metus vulputate eu scelerisque. Urna duis convallis convallis tellus id interdum. Imperdiet sed euismod nisi porta lorem mollis.

Nibh mauris cursus mattis molestie a iaculis. Ac turpis egestas maecenas pharetra convallis posuere morbi leo urna. Ultricies mi eget mauris pharetra et. Facilisi cras fermentum odio eu feugiat. Non tellus orci ac auctor augue mauris augue neque gravida. Metus vulputate eu scelerisque felis. Et leo duis ut diam quam nulla porttitor. Eu lobortis elementum nibh tellus molestie nunc non. Semper quis lectus nulla at volutpat diam ut venenatis. Lectus proin nibh nisl condimentum id venenatis a.
        """.trimIndent() }
    }

    private fun debug(_view: View) {
        Birch.d { "debug message" }
    }

    private fun info(_view: View) {
        Birch.i { "info text" }
    }

    private fun warn(_view: View) {
        Birch.w { "warn msg" }
    }

    private fun error(_view: View) {
        Birch.e { "error alert" }
    }

    private fun stressTest(_view: View) {
        if (isStressTesting) {
            return
        }
        isStressTesting = true
        val threads = mutableListOf<Thread>()

        repeat(4) {
            val tid = "thread-$it"
            threads.add(
                thread {
                    repeat(5_000) {
                        Birch.d { "$tid - $it" }
                        Thread.sleep((10..50).random().toLong())
                    }
                }
            )
        }

        threads.forEach { it.join() }
        isStressTesting = false
    }

    private fun translate(bool: Boolean): String {
        return if (bool) "ON" else "OFF"
    }

    @SuppressLint("SetTextI18n")
    private fun setState() {
        toggleDebugButton.text = "Debug ${translate(Birch.debug)}"
        toggleLevelButton.text = "Level ${Birch.level}"
        toggleConsoleButton.text = "Console ${translate(Birch.console)}"
        toggleRemoteButton.text = "Remote ${translate(Birch.remote)}"
        toggleSynchronousButton.text = "Synchronous ${translate(Birch.synchronous)}"
    }
}