package cmpsd.farmingutils.item;

public interface IUpgradable {

	public static enum Tier {
		AMATEUR(0, 1, 32),
		APPRENTICE(1, 3, 64),
		ADULT(2, 5, 128),
		EXPERT(3, 7, 512),
		MASTER(4, 9, 2048);

		private final int id;
		private final int range;
		private final int durability;

		private Tier(int idIn, int rangeIn, int durabilityIn) {
			this.id = idIn;
			this.range = rangeIn;
			this.durability = durabilityIn;
		}

		public int getId() {
			return this.id;
		}

		public int getRange() {
			return this.range;
		}

		public int getDurability() {
			return this.durability;
		}
	}
}
