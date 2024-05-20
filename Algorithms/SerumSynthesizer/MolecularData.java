import java.util.*;

// Class representing molecular data
public class MolecularData {

    // Private fields
    private final List<Molecule> molecules; // List of molecules

    // Constructor
    public MolecularData(List<Molecule> molecules) {
        this.molecules = molecules;
    }

    // Getter for molecules
    public List<Molecule> getMolecules() {
        return molecules;
    }

    // Method to identify molecular structures
    // Return the list of different molecular structures identified from the input data
    public List<MolecularStructure> identifyMolecularStructures() {
        ArrayList<MolecularStructure> structures = new ArrayList<>();

        /* YOUR CODE HERE */
        int[] id = new int[molecules.size()];
        int curId = 1;
        for(int i = 0; i < molecules.size(); i++){
            if(id[i] != 0) continue;
            boolean mistaken = false;
            List<String> bonds = molecules.get(i).getBonds();
            for(String bond : bonds){
                Molecule bondyMol = null;
                for(Molecule mol : molecules){
                    if(mol.getId().equals(bond)) {
                        bondyMol = mol;
                        break;
                    }
                }
                if(id[molecules.indexOf(bondyMol)] != 0){
                    dfs(molecules.get(i), id, id[molecules.indexOf(bondyMol)]);
                    mistaken = true;
                }
            }
            if(!mistaken){
                dfs(molecules.get(i), id, curId);
                curId++;
            }
        }

        for(int i = 1; i < curId; i++){
            MolecularStructure struct = new MolecularStructure();
            structures.add(struct);
        }

        for(int i = 0; i < id.length; i++){
            structures.get(id[i] - 1).addMolecule(molecules.get(i));
        }

        return structures;
    }

    public void dfs(Molecule mol, int[] id, int curId){
        List<String> bonds = mol.getBonds();
        id[molecules.indexOf(mol)] = curId;
        for(String bond : bonds){
            Molecule bondyMol = null;
            for(Molecule mainMol : molecules){
                if(mainMol.getId().equals(bond)){
                    bondyMol = mainMol;
                    break;
                }
            }

            if(id[molecules.indexOf(bondyMol)] != 0) continue;
            dfs(bondyMol, id, curId);
        }
    }

    // Method to print given molecular structures
    public void printMolecularStructures(List<MolecularStructure> molecularStructures, String species) {
        
        /* YOUR CODE HERE */
        System.out.print(molecularStructures.size());
        if(species.equals("typical humans")){
            System.out.println(" molecular structures have been discovered in typical humans.");
        } else{
            System.out.println(" molecular structures have been discovered in Vitales individuals.");
        }

        for(int i = 0; i < molecularStructures.size(); i++){
            System.out.print("Molecules in Molecular Structure " + (i + 1) + ": ");
            System.out.println(molecularStructures.get(i).toString());
        }

    }

    // Method to identify anomalies given a source and target molecular structure
    // Returns a list of molecular structures unique to the targetStructure only
    public static ArrayList<MolecularStructure> getVitalesAnomaly(List<MolecularStructure> sourceStructures, List<MolecularStructure> targeStructures) {
        ArrayList<MolecularStructure> anomalyList = new ArrayList<>();
        
        /* YOUR CODE HERE */
        for(MolecularStructure struct : targeStructures){
            boolean found = false;
            for(MolecularStructure structHuman : sourceStructures){
                if(struct.equals(structHuman)) found = true;
            }
            if(!found) anomalyList.add(struct);
        }

        return anomalyList;
    }

    // Method to print Vitales anomalies
    public void printVitalesAnomaly(List<MolecularStructure> molecularStructures) {

        /* YOUR CODE HERE */ 

        System.out.println("Molecular structures unique to Vitales individuals: ");
        for(MolecularStructure struct : molecularStructures){
            System.out.println(struct.toString());
        }
    }
}
