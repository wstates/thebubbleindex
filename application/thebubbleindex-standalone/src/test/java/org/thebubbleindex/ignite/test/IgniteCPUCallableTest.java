package org.thebubbleindex.ignite.test;

import org.cactoos.io.ResourceOf;
import org.cactoos.text.TextOf;
import org.junit.Test;
import org.thebubbleindex.computegrid.BubbleIndexComputeGrid;
import org.thebubbleindex.computegrid.IgniteBubbleIndexComputeGrid;
import org.thebubbleindex.driver.BubbleIndex;
import org.thebubbleindex.driver.DailyDataCache;
import org.thebubbleindex.inputs.Indices;
import org.thebubbleindex.logging.Logs;
import org.thebubbleindex.runnable.RunContext;
import org.thebubbleindex.testutil.TestUtil;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import static org.junit.Assert.assertEquals;

public class IgniteCPUCallableTest {

	final double epsilon = 0.01;
	final double epsilonAlt = 0.1;
	final String fileSep = File.separator;

	@Test
	public void resultsShouldMatchBITSTAMPUSD() throws IOException, URISyntaxException {
		final Indices indices = new Indices();
		indices.initialize();

		final BubbleIndexComputeGrid bubbleIndexComputeGrid = new IgniteBubbleIndexComputeGrid();

		final RunContext runContext = new RunContext(false, true);
		runContext.setThreadNumber(4);
		runContext.setForceCPU(true);

		final DailyDataCache dailyDataCache = new DailyDataCache();

		final String selectionName = "BITSTAMPUSD";
		final String folder = "ProgramData";
		final String folderType = "Currencies";
		final double omegaDouble = 6.28;
		final double mCoeffDouble = 0.38;
		final double tCritDouble = 21.0;

		final String pathRoot = folder + fileSep + folderType + fileSep + selectionName + fileSep + selectionName;
		final ResourceOf dailyDataResource = new ResourceOf(pathRoot + "dailydata.csv");

		final List<String> lines = TestUtil.getLines(new TextOf(dailyDataResource).asString());
		final List<String> dailyPriceDate = new ArrayList<String>();
		final List<String> dailyPriceData = new ArrayList<String>();

		parseDailyData(lines, dailyPriceData, dailyPriceDate);

		final int dataSize = dailyPriceDate.size();
		final double[] dailyPriceValues = new double[dataSize];
		for (int i = 0; i < dataSize; i++) {
			dailyPriceValues[i] = Double.parseDouble(dailyPriceData.get(i));
		}

		dailyDataCache.setDailyPriceData(dailyPriceData);
		dailyDataCache.setDailyPriceDate(dailyPriceDate);
		dailyDataCache.setDailyPriceDoubleValues(dailyPriceValues);
		dailyDataCache.setSelectionName(selectionName);

		Logs.myLogger.info("Running single selection. Category Name = {}, Selection Name = {}", folderType,
				selectionName);

		final String[] windowArray = new String[] { "52", "104", "153", "256", "512" };

		for (final String window : windowArray) {
			final BubbleIndex bubbleIndex = new BubbleIndex(omegaDouble, mCoeffDouble, tCritDouble,
					Integer.parseInt(window.trim()), folderType, selectionName, dailyDataCache, indices, null,
					runContext);
			bubbleIndexComputeGrid.addBubbleIndexTask(bubbleIndex.hashCode(), bubbleIndex);
		}

		bubbleIndexComputeGrid.deployTasks();

		final List<BubbleIndex> results = bubbleIndexComputeGrid.executeBubbleIndexTasks();

		compareResults(selectionName, results);
		results.clear();

		bubbleIndexComputeGrid.shutdownGrid();
	}

	@Test
	public void resultsShouldMatchTSLA() throws IOException, URISyntaxException {
		final Indices indices = new Indices();
		indices.initialize();

		final BubbleIndexComputeGrid bubbleIndexComputeGrid = new IgniteBubbleIndexComputeGrid();

		final RunContext runContext = new RunContext(false, true);
		runContext.setThreadNumber(4);
		runContext.setForceCPU(true);
		
		final DailyDataCache dailyDataCache = new DailyDataCache();

		final String selectionName = "TSLA";
		final String folder = "ProgramData";
		final String folderType = "Stocks";
		final double omegaDouble = 6.28;
		final double mCoeffDouble = 0.38;
		final double tCritDouble = 21.0;

		final String pathRoot = folder + fileSep + folderType + fileSep + selectionName + fileSep + selectionName;
		final ResourceOf dailyDataResource = new ResourceOf(pathRoot + "dailydata.csv");

		final List<String> lines = TestUtil.getLines(new TextOf(dailyDataResource).asString());
		final List<String> dailyPriceDate = new ArrayList<String>();
		final List<String> dailyPriceData = new ArrayList<String>();

		parseDailyData(lines, dailyPriceData, dailyPriceDate);

		final int dataSize = dailyPriceDate.size();
		final double[] dailyPriceValues = new double[dataSize];
		for (int i = 0; i < dataSize; i++) {
			dailyPriceValues[i] = Double.parseDouble(dailyPriceData.get(i));
		}

		dailyDataCache.setDailyPriceData(dailyPriceData);
		dailyDataCache.setDailyPriceDate(dailyPriceDate);
		dailyDataCache.setDailyPriceDoubleValues(dailyPriceValues);
		dailyDataCache.setSelectionName(selectionName);

		Logs.myLogger.info("Running single selection. Category Name = {}, Selection Name = {}", folderType,
				selectionName);

		final String[] windowArray = new String[] { "52", "104", "153", "256", "512" };

		for (final String window : windowArray) {
			final BubbleIndex bubbleIndex = new BubbleIndex(omegaDouble, mCoeffDouble, tCritDouble,
					Integer.parseInt(window.trim()), folderType, selectionName, dailyDataCache, indices, null,
					runContext);
			bubbleIndexComputeGrid.addBubbleIndexTask(bubbleIndex.hashCode(), bubbleIndex);
		}

		bubbleIndexComputeGrid.deployTasks();

		final List<BubbleIndex> results = bubbleIndexComputeGrid.executeBubbleIndexTasks();

		compareResults(selectionName, results);
		results.clear();

		bubbleIndexComputeGrid.shutdownGrid();
	}

	private void compareResults(final String selectionName, final List<BubbleIndex> bubbleIndexResults)
			throws IOException, URISyntaxException {
		assert bubbleIndexResults.size() > 0;

		for (final BubbleIndex bubbleIndex : bubbleIndexResults) {
			final List<Double> results = bubbleIndex.getResults();
			final URL resultURL = getClass().getClassLoader().getResource("sample-results" + fileSep + selectionName
					+ fileSep + selectionName + String.valueOf(bubbleIndex.getWindow()) + "days.csv");
			final Path resultPath = new File(resultURL.toURI()).toPath();
			final List<String> lines = Files.readAllLines(resultPath, Charset.defaultCharset());

			int index = 0;
			for (final String line : lines) {
				if (index == 0) {
					index++;
					continue;// header
				}
				final Scanner lineScan = new Scanner(line);
				lineScan.useDelimiter(",|\t");
				lineScan.next();// index
				final Double expected = Double.parseDouble(lineScan.next());// value
				lineScan.next();// date
				lineScan.close();

				assertEquals(expected, results.get(index - 1), epsilon * expected);

				index++;
			}
		}
	}

	private void parseDailyData(final List<String> lines, final List<String> priceValues,
			final List<String> dailyPriceDate) {
		for (final String line : lines) {
			final Scanner lineScan = new Scanner(line);
			lineScan.useDelimiter(",|\t");
			dailyPriceDate.add(lineScan.next());
			priceValues.add(lineScan.next());
			lineScan.close();
		}
	}
}
