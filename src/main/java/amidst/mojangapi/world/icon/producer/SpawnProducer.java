package amidst.mojangapi.world.icon.producer;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import amidst.documentation.ThreadSafe;
import amidst.logging.Log;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.icon.WorldIcon;
import amidst.mojangapi.world.icon.type.DefaultWorldIconTypes;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

@ThreadSafe
public class SpawnProducer extends CachedWorldIconProducer {
	private final long seed;
	private final BiomeDataOracle biomeDataOracle;
	private final List<Biome> validBiomes;

	public SpawnProducer(long seed, BiomeDataOracle biomeDataOracle,
			List<Biome> validBiomes) {
		this.seed = seed;
		this.biomeDataOracle = biomeDataOracle;
		this.validBiomes = validBiomes;
	}

	@Override
	protected List<WorldIcon> doCreateCache() {
		return Arrays.asList(createSpawnWorldIcon());
	}

	private WorldIcon createSpawnWorldIcon() {
		CoordinatesInWorld spawnLocation = getSpawnCenterInWorldCoordinates();
		if (spawnLocation != null) {
			return createWorldIcon(spawnLocation);
		} else {
			CoordinatesInWorld origin = CoordinatesInWorld.origin();
			Log.i("Unable to find spawn biome. Falling back to "
					+ origin.toString() + ".");
			return createWorldIcon(origin);
		}
	}

	private WorldIcon createWorldIcon(CoordinatesInWorld coordinates) {
		return new WorldIcon(coordinates,
				DefaultWorldIconTypes.SPAWN.getName(),
				DefaultWorldIconTypes.SPAWN.getImage(), Dimension.OVERWORLD);
	}

	private CoordinatesInWorld getSpawnCenterInWorldCoordinates() {
		return biomeDataOracle.findValidLocation(0, 0, 256, validBiomes,
				new Random(seed));
	}
}