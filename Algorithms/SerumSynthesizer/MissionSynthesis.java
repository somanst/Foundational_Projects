import java.util.*;

// Class representing the Mission Synthesis
public class MissionSynthesis {

    // Private fields
    private final List<MolecularStructure> humanStructures; // Molecular structures for humans
    private final ArrayList<MolecularStructure> diffStructures; // Anomalies in Vitales structures compared to humans

    // Constructor
    public MissionSynthesis(List<MolecularStructure> humanStructures, ArrayList<MolecularStructure> diffStructures) {
        this.humanStructures = humanStructures;
        this.diffStructures = diffStructures;
    }

    private List<Molecule> lowestFinder(List<MolecularStructure> structures){
        List<Molecule> lowest = new ArrayList<>();
        for(MolecularStructure struct : structures){
            int lowestBond = Integer.MAX_VALUE;
            int lowestIndex = -1;
            for(int i = 0; i < struct.getMolecules().size(); i++){
                if(struct.getMolecules().get(i).getBondStrength() < lowestBond){
                    lowestIndex = i;
                    lowestBond = struct.getMolecules().get(i).getBondStrength();
                }
            }
            lowest.add(struct.getMolecules().get(lowestIndex));
        }
        return lowest;
    }

    private List<Bond> bondCalc(List<Molecule> mols){
        List<Bond> bonds = new ArrayList<>();
        for(int i = 0; i < mols.size() - 1; i++){
            for(int j = i + 1; j < mols.size(); j++){
                double bondStrength = (mols.get(j).getBondStrength() + mols.get(i).getBondStrength())/2.0;
                Bond tempBond = new Bond(mols.get(j), mols.get(i), bondStrength);
                bonds.add(tempBond);
            }
        }
        return bonds;
    }

    // Method to synthesize bonds for the serum
    public List<Bond> synthesizeSerum() {
        List<Bond> serum = new ArrayList<>();

        /* YOUR CODE HERE */
        List<Molecule> lowestBonds = lowestFinder(humanStructures);
        lowestBonds.addAll(lowestFinder(diffStructures));
        List<Bond> potentialBonds = bondCalc(lowestBonds);

        potentialBonds.sort(Comparator.comparing(Bond::getWeight));
        List<Molecule> visited = new ArrayList<>();
        for(Bond bond : potentialBonds){
            if(!(visited.contains(bond.getTo()) & visited.contains(bond.getFrom()))){
                visited.add(bond.getTo());
                visited.add(bond.getFrom());
                serum.add(bond);
            }
        }


        return serum;
    }

    // Method to print the synthesized bonds
    public void printSynthesis(List<Bond> serum) {

        /* YOUR CODE HERE */
        List<Molecule> humanSelect = lowestFinder(humanStructures);
        List<Molecule> vitealSelect = lowestFinder(diffStructures);

        System.out.print("Typical human molecules selected for synthesis: ");
        System.out.println(humanSelect);
        System.out.print("Vitales molecules selected for synthesis: ");
        System.out.println(vitealSelect);

        System.out.println("Synthesizing the serum...");

        float sum = 0;
        for(Bond bond : serum){
            String prevStr = bond.getFrom().getId();
            String nextStr = bond.getTo().getId();
            if(Integer.parseInt(prevStr.substring(1)) > Integer.parseInt(nextStr.substring(1))){
                prevStr = nextStr;
                nextStr = bond.getFrom().getId();
            }

            System.out.printf("Forming a bond between %s - %s with strength %.2f\n", prevStr, nextStr, bond.getWeight());
            sum += bond.getWeight();
        }
        System.out.printf("The total serum bond strength is %.2f\n", sum);
    }
}
