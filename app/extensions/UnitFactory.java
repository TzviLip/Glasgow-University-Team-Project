package extensions;

import java.util.ArrayList;

import structures.GameState;
import structures.basic.Tile;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;
/*This class follows the Factory Design Pattern and is used to instantiate all the PlayableUnits subclasses. 
 *This UnitFactory will be passed an ID, which will then return a reference to the unit to be played.
 */
public class UnitFactory {

	GameState gameState;

	ArrayList<String> units = new ArrayList<String>() {
		{
			add(StaticConfFiles.u_azure_herald); // ID = 0
			add(StaticConfFiles.u_azure_herald); // ID = 1
			add(StaticConfFiles.u_azurite_lion); // ID = 2
			add(StaticConfFiles.u_azurite_lion); // ID = 3
			add(StaticConfFiles.u_comodo_charger); // ID = 4
			add(StaticConfFiles.u_comodo_charger); // ID = 5
			add(StaticConfFiles.u_fire_spitter); // ID = 6
			add(StaticConfFiles.u_fire_spitter); // ID = 7
			add(StaticConfFiles.u_hailstone_golem); // ID = 8
			add(StaticConfFiles.u_hailstone_golem); // ID = 9
			add(StaticConfFiles.u_ironcliff_guardian); // ID = 10
			add(StaticConfFiles.u_ironcliff_guardian); // ID = 11
			add(StaticConfFiles.u_pureblade_enforcer); // ID = 12
			add(StaticConfFiles.u_pureblade_enforcer); // ID = 13
			add(StaticConfFiles.u_silverguard_knight); // ID = 14
			add(StaticConfFiles.u_silverguard_knight); // ID = 15
		}
	};

	ArrayList<String> enemyUnits = new ArrayList<String>() {
		{
			add(StaticConfFiles.u_blaze_hound); //ID = 20
			add(StaticConfFiles.u_blaze_hound); //ID = 21
			add(StaticConfFiles.u_bloodshard_golem); //ID = 22
			add(StaticConfFiles.u_bloodshard_golem); //ID = 23
			add(StaticConfFiles.u_hailstone_golemR); //ID = 24
			add(StaticConfFiles.u_hailstone_golemR); //ID = 25
			add(StaticConfFiles.u_planar_scout); //ID = 26
			add(StaticConfFiles.u_planar_scout); //ID = 27
			add(StaticConfFiles.u_pyromancer); //ID = 28
			add(StaticConfFiles.u_pyromancer); //ID = 29
			add(StaticConfFiles.u_rock_pulveriser); //ID = 30
			add(StaticConfFiles.u_rock_pulveriser); //ID = 31
			add(StaticConfFiles.u_serpenti); //ID = 32
			add(StaticConfFiles.u_serpenti); //ID = 33
			add(StaticConfFiles.u_windshrike); //ID = 34
			add(StaticConfFiles.u_windshrike); //ID = 35
		}
	};

	public UnitFactory(GameState gameState) {
		this.gameState = gameState;
	}

	//Return a specific playable unit depending on the ID given
	public PlayableUnit makeUnit(int ID, Tile tile) {
		PlayableUnit unit = null;
		switch (ID) {
		case 0:
		case 1:
			// u_azure_herald
			unit = getOnSummonUnit(ID);
			setStats(unit, 1, 4, true, tile, "Azure Herald");
			break;
		case 2:
		case 3:
			// u_azurite_lion
			unit = getAttackTwiceUnit(ID);
			setStats(unit, 2, 3, true, tile, "Azurite Lion");
			break;
		case 4:
		case 5:
			// u_comodo_charger
			unit = getStandardUnit(ID);
			setStats(unit, 1, 3, true, tile, "Comodo Charger");
			break;
		case 6:
		case 7:
			// u_fire_spitter
			unit = getRangedUnit(ID);
			setStats(unit, 3, 2, true, tile, "Fire Spitter");
			break;
		case 8:
			;
		case 9:
			// u_hailstone_golem
			unit = getStandardUnit(ID);
			setStats(unit, 4, 6, true, tile, "Hailstone Golem (Human Player)");
			break;
		case 10:
		case 11:
			// u_ironcliff_guardian
			unit = getStandardUnit(ID);
			setStats(unit, 3, 10, true, tile, "Ironcliff Guardian");
			unit.setProvoke(true);
			break;
		case 12:
		case 13:
			// u_pureblade_enforcer
			unit = getSpellThiefUnit(ID);
			setStats(unit, 1, 4, true, tile, "Pureblade Enforcer");
			spellThief(unit);
			break;
		case 14:
		case 15:
			// u_silverguard_knight
			unit = getTrigDamageUnit(ID);
			setStats(unit, 1, 5, true, tile, "Silverguard Knight");			
			trigDamageUnitReg(unit);
			unit.setProvoke(true);
			break;
		case 20:
		case 21:
			// u_blaze_hound
			unit = getOnSummonUnit(ID);
			setStats(unit, 4, 3, false, tile, "Blaze Hound");
			break;
		case 22:
		case 23:
			// u_bloodshard_golem
			unit = getStandardUnit(ID);
			setStats(unit, 4, 3, false, tile, "Bloodshard Golem");
			break;
		case 24:
		case 25:
			// u_hailstone_golemR
			unit = getStandardUnit(ID);
			setStats(unit, 4, 6, false, tile, "Hailstone Golem (AI Player)");
			break;
		case 26:
		case 27:
			// u_planar_scout
			unit = getStandardUnit(ID);
			setStats(unit, 2, 1, false, tile, "Planar Scout");
			break;
		case 28:
		case 29:
			// u_pyromancer
			unit = getRangedUnit(ID);
			setStats(unit, 2, 1, false, tile, "Pyromancer");
			break;
		case 30:
		case 31:
			// u_rock_pulveriser
			unit = getStandardUnit(ID);
			setStats(unit, 1, 4, false, tile, "Rock Pulveriser");
			unit.setProvoke(true);
			break;
		case 32:
		case 33:
			// u_serpenti
			unit = getAttackTwiceUnit(ID);
			setStats(unit, 7, 4, false, tile, "Serpenti");
			break;
		case 34:
		case 35:
			// u_windshrike
			unit = getOnDeathUnit(ID);
			setStats(unit, 4, 3, false, tile, "Windshrike");
			unit.makeItFly(true);
			break;
		case 100:
			// Player Avatar
			unit = getHumanAvatar();
			setStats(unit, 2, 20, true, tile, "Human Avatar");
			break;
		case 101:
			// Enemy Avatar
			unit = getAIAvatar();
			setStats(unit, 2, 20, false, tile, "AI Avatar");
			break;
		default:
			//Invalid ID
			System.err.println("Invalid ID");
			break;
		}
		
		return unit;
	}

	//Create a unit with no method based special abilities - boolean special abilities use this method
	private PlayableUnit getStandardUnit(int ID) {
		return (PlayableUnit) BasicObjectBuilders.loadUnit(getUnitByID(ID), ID, PlayableUnit.class);
	}
	
	//Create a unit with a Death Trigger
	private PlayableUnit getOnDeathUnit(int ID) {
		return (PlayableUnit) BasicObjectBuilders.loadUnit(getUnitByID(ID), ID, OnDeathUnit.class);
	}
	
	//Create a unit with a Summon Trigger
	private PlayableUnit getOnSummonUnit(int ID) {
		return (PlayableUnit) BasicObjectBuilders.loadUnit(getUnitByID(ID), ID, SummonUnit.class);
	}
	
	//Create a unit with the Ranged Ability
	private PlayableUnit getRangedUnit(int ID) {
		return (PlayableUnit) BasicObjectBuilders.loadUnit(getUnitByID(ID), ID, RangedUnit.class);
	}

	//Create a unit with a Triggered Ability
	private PlayableUnit getTrigDamageUnit(int ID) {
		return (PlayableUnit) BasicObjectBuilders.loadUnit(getUnitByID(ID), ID, TrigDamageUnit.class);
	}
	
	//Create a unit which can attack twice
	private PlayableUnit getAttackTwiceUnit(int ID) {
		return (PlayableUnit) BasicObjectBuilders.loadUnit(getUnitByID(ID), ID, AttackTwiceUnit.class);
	}
	
	//Create a unit with the SpellThief Ability
	private PlayableUnit getSpellThiefUnit(int ID) {
		return (PlayableUnit) BasicObjectBuilders.loadUnit(getUnitByID(ID), ID, SpellThiefUnit.class);
	}
	
	//Create a Human Avatar
	private Avatar getHumanAvatar() {
		return (Avatar) BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 100, Avatar.class);
	}

	//Create an AI Avatar
	private Avatar getAIAvatar() {
		return (Avatar) BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, 101, Avatar.class);
	}

	//Set a units stats
	private void setStats(PlayableUnit unit, int attack, int health, boolean friendly, Tile tile, String name) {
		unit.setAttack(attack);
		unit.setHealth(health);
		unit.setMaxHealth(health);
		unit.setFriendly(friendly);
		unit.currentTile = tile;
		unit.setCanAttack(false); //Stop attacking on the first turn played
		unit.setCanMove(false); //Stop moving on the first turn played
		unit.setProvoke(false);
		unit.setName(name);
		setUnitPlayer(unit);
		System.out.println("New unit created: " + name);
	}

	//Given an ID, get the relevant unit from the correct ArrayList
	private String getUnitByID(int ID) {
		if (ID < 20) {
		return units.get(ID);
		}else {
			ID -= 20; //to get the correct element of the array
			return enemyUnits.get(ID);
		}
	}
	
	//Set player to human or AI depending ID number
	private void setUnitPlayer(PlayableUnit unit) {
		int ID = unit.getId();
		if (ID < 20 || ID == 100) {
			unit.setPlayer(gameState.getHumanPlayer());
		} else {
			unit.setPlayer(gameState.getAiPlayer());
		}
	}

	//Used to set the Subject field on the Trigger Unit
	private void trigDamageUnitReg(PlayableUnit unit){
		if(unit instanceof TrigDamageUnit){
			TrigDamageUnit trigDamageUnit = (TrigDamageUnit) unit;
			trigDamageUnit.setSubjectAvatar();
		}
	}
	
	/* Set gameState as Subject of SpellThiefUnit instance */
	private void spellThief(PlayableUnit unit) {
		if(unit instanceof SpellThiefUnit){
			SpellThiefUnit stUnit = (SpellThiefUnit) unit;
			stUnit.setSubject(gameState);
		}
	}

}

