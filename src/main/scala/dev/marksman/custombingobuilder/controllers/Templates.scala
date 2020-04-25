package dev.marksman.custombingobuilder.controllers

import javax.inject.Inject
import play.api.mvc.{AnyContent, BaseController, ControllerComponents, Request}

class Templates @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  def simple5x5() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.cards.simple_5x5())
  }

}
