/*
 * This file is part of the PSL software.
 * Copyright 2011-2015 University of Maryland
 * Copyright 2013-2017 The Regents of the University of California
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.linqs.psl.reasoner.admm;

import static org.junit.Assert.assertEquals;

import java.util.Vector;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Before;
import org.junit.Test;
import org.linqs.psl.config.ConfigBundle;
import org.linqs.psl.config.ConfigManager;
import org.linqs.psl.reasoner.admm.ADMMReasoner;
import org.linqs.psl.reasoner.admm.SquaredLinearLossTerm;

public class SquaredLinearLossTermTest {
	
	private ConfigBundle config;
	
	@Before
	public final void setUp() throws ConfigurationException {
		ConfigManager manager = ConfigManager.getManager();
		config = manager.getBundle("dummy");
	}

	@Test
	public void testMinimize() {
		/*
		 * Problem 1
		 */
		double[] z = {0.4, 0.5, 0.1};
		double[] y = {0.0, 0.0, -0.05};
		double[] coeffs = {0.3, -1.0, 0.4};
		double constant = -20.0;
		double weight = 0.5;
		double stepSize = 2.0;
		double[] expected = {-1.41569, 6.55231, -2.29593};
		testProblem(z, y, coeffs, constant, weight, stepSize, expected);
	}
	
	private void testProblem(double[] z, double[] y, double[] coeffs, double constant,
			double weight, final double stepSize, double[] expected) {
		config.setProperty("admmreasoner.stepsize", stepSize);
		ADMMReasoner reasoner = new ADMMReasoner(config);
		reasoner.z = new Vector<Double>(z.length);
		for (int i = 0; i < z.length; i++)
			reasoner.z.add(z[i]);
		
		int[] zIndices = new int[z.length];
		for (int i = 0; i < z.length; i++)
			zIndices[i] = i;
		SquaredLinearLossTerm term = new SquaredLinearLossTerm(reasoner, zIndices, coeffs, constant, weight);
		for (int i = 0; i < z.length; i++)
			term.y[i] = y[i];
		term.minimize();
		
		for (int i = 0; i < z.length; i++)
			assertEquals(expected[i], term.x[i], 5e-5);
	}

}
