package bootstrap

import com.google.inject.AbstractModule

class BootstrapModule extends AbstractModule {

  protected def configure: Unit = bind(classOf[InitialData]).asEagerSingleton()

}