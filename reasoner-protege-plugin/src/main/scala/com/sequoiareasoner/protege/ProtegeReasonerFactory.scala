/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This file is part of Sequoia, an OWL 2 reasoner that supports the SRIQ subset of OWL 2 DL.
 * Copyright 2017 by Andrew Bate <code@andrewbate.com>.
 *
 * This code is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 only,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License version 3 for more details (a copy is
 * included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU General Public License
 * version 3 along with this work.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.sequoiareasoner.protege

import com.sequoiareasoner.kernel.logging.Logger
import com.sequoiareasoner.kernel.reasoner.ReasonerConfiguration
import com.sequoiareasoner.kernel.structural.UnsupportedFeatureObserver
import org.protege.editor.owl.model.inference.AbstractProtegeOWLReasonerInfo
import com.sequoiareasoner.owlapi._
import org.semanticweb.owlapi.reasoner._
import com.sequoiareasoner.protege.ui.UnsupportedWarningDialogs

/** Entry point for the Sequoia reasoner plugin for Protégé.
  *
  * @author Andrew Bate <code@andrewbate.com>
  */
class ProtegeReasonerFactory extends AbstractProtegeOWLReasonerInfo {

  override def getRecommendedBuffering: BufferingMode = {
    SequoiaReasonerPreferences.load
//    if (SequoiaReasonerPreferences.incrementalMode && SequoiaReasonerPreferences.autoSynchronization)
//      return BufferingMode.NON_BUFFERING
    BufferingMode.BUFFERING
  }

  override def getReasonerFactory: OWLReasonerFactory = SequoiaReasonerFactory.getInstance

  override def getConfiguration(monitor: ReasonerProgressMonitor): OWLReasonerConfiguration = {
    val unsupportedWarningDialogs = new UnsupportedWarningDialogs
    import SequoiaReasonerPreferences._
    new SequoiaReasonerConfiguration(
      progressMonitor = new SequoiaReasonerProgressMonitor(monitor),
      freshEntityPolicy = if (allowFreshEntities) FreshEntityPolicy.ALLOW else FreshEntityPolicy.DISALLOW,
      timeOut = timeout,
      individualNodeSetPolicy = if (defaultIndividualNodeSetSameAs) IndividualNodeSetPolicy.BY_SAME_AS else IndividualNodeSetPolicy.BY_NAME,
      unsupportedFeatureTreatment = UnsupportedFeatureTreatment.IGNORE,
      unsupportedAPIMethodHandler = unsupportedWarningDialogs,
      enableMultithreading = enableMultithreading,
      enableEqualitySimplifyReflect = defaultEnableEqualitySimplifyReflect,
      enableTrieRedundancyIndex = defaultEnableTrieRedundancyIndex,
      enableEqualityReasoning = enableEqualityReasoning,
    ) {
      override def getUnsupportedFeatureObserver(logger: Logger): UnsupportedFeatureObserver = unsupportedWarningDialogs
      override def getUnsupportedSWRLRuleHandler(logger: Logger): UnsupportedSWRLRuleHandler = unsupportedWarningDialogs
    }

  }

}