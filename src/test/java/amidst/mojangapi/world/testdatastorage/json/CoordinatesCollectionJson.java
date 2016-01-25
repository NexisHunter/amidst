package amidst.mojangapi.world.testdatastorage.json;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;
import amidst.fragment.Fragment;
import amidst.mojangapi.mocking.FragmentCornerWalker;
import amidst.mojangapi.mocking.NameFilteredWorldIconCollector;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.icon.WorldIcon;
import amidst.mojangapi.world.icon.producer.WorldIconProducer;

@Immutable
public class CoordinatesCollectionJson {
	public static <T> CoordinatesCollectionJson fromWorldIconProducer(
			WorldIconProducer<T> producer, String name,
			Function<CoordinatesInWorld, T> additionalDataFactory,
			int fragmentsAroundOrigin) {
		NameFilteredWorldIconCollector consumer = new NameFilteredWorldIconCollector(
				name);
		long blocksAroundOrigin = fragmentsAroundOrigin * Fragment.SIZE;
		CoordinatesInWorld startCorner = CoordinatesInWorld.from(
				-blocksAroundOrigin, -blocksAroundOrigin);
		CoordinatesInWorld endCorner = startCorner.add(2 * blocksAroundOrigin,
				2 * blocksAroundOrigin);
		new FragmentCornerWalker(startCorner, endCorner).walk(producer,
				consumer, additionalDataFactory);
		return fromWorldIcons(consumer.get());
	}

	public static CoordinatesCollectionJson fromWorldIcons(List<WorldIcon> icons) {
		long[][] coordinatesArray = icons.stream()
				.map(CoordinatesCollectionJson::createCoordinatesArrayEntry)
				.toArray(size -> new long[size][]);
		return new CoordinatesCollectionJson(coordinatesArray);
	}

	private static long[] createCoordinatesArrayEntry(WorldIcon icon) {
		return createCoordinatesArrayEntry(icon.getCoordinates());
	}

	public static CoordinatesCollectionJson fromCoordinates(
			List<CoordinatesInWorld> coordinatesList) {
		long[][] coordinatesArray = coordinatesList.stream()
				.map(CoordinatesCollectionJson::createCoordinatesArrayEntry)
				.toArray(size -> new long[size][]);
		return new CoordinatesCollectionJson(coordinatesArray);
	}

	private static long[] createCoordinatesArrayEntry(
			CoordinatesInWorld coordinates) {
		return new long[] { coordinates.getX(), coordinates.getY() };
	}

	private volatile long[][] coordinatesArray;

	@GsonConstructor
	public CoordinatesCollectionJson() {
	}

	public CoordinatesCollectionJson(long[][] coordinatesArray) {
		this.coordinatesArray = coordinatesArray;
	}

	public boolean contains(CoordinatesInWorld coordinates) {
		for (long[] coordinatesArrayEntry : coordinatesArray) {
			if (Arrays.equals(createCoordinatesArrayEntry(coordinates),
					coordinatesArrayEntry)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(coordinatesArray);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof CoordinatesCollectionJson)) {
			return false;
		}
		CoordinatesCollectionJson other = (CoordinatesCollectionJson) obj;
		if (!Arrays.deepEquals(coordinatesArray, other.coordinatesArray)) {
			return false;
		}
		return true;
	}
}
