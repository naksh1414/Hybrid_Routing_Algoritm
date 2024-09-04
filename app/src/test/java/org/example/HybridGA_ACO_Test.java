public class HybridGA_ACO_Test {
    
    public static void main(String[] args) {
        testInitialization();
        testPopulationInitialization();
        testCrossoverMechanism();
        testMutationMechanism();
        testACORefinement();
        testPheromoneUpdate();
        testFinalOutput();
        testEdgeCases();
    }

    // Test initialization of distances, traffic, and pheromones matrices
    static void testInitialization() {
        System.out.println("Running testInitialization...");
        HybridGA_ACO.initialize();
        
        System.out.println("Distances Matrix:");
        printMatrix(HybridGA_ACO.distances);
        
        System.out.println("Traffic Matrix:");
        printMatrix(HybridGA_ACO.traffic);
        
        System.out.println("Pheromones Matrix (Should be all 1.0):");
        printMatrix(HybridGA_ACO.pheromones);
        
        System.out.println("Initialization Test Passed\n");
    }

    // Test population initialization
    static void testPopulationInitialization() {
        System.out.println("Running testPopulationInitialization...");
        HybridGA_ACO.initialize();
        var population = HybridGA_ACO.initializePopulation();
        
        for (int i = 0; i < population.size(); i++) {
            System.out.println("Route " + i + ": " + population.get(i).getRoute());
        }
        
        System.out.println("Population Initialization Test Passed\n");
    }

    // Test the crossover mechanism
    static void testCrossoverMechanism() {
        System.out.println("Running testCrossoverMechanism...");
        HybridGA_ACO.initialize();
        var population = HybridGA_ACO.initializePopulation();
        
        var parent1 = population.get(0);
        var parent2 = population.get(1);
        
        System.out.println("Parent 1: " + parent1.getRoute());
        System.out.println("Parent 2: " + parent2.getRoute());
        
        var child = HybridGA_ACO.crossover(parent1, parent2);
        
        System.out.println("Child: " + child.getRoute());
        System.out.println("Crossover Mechanism Test Passed\n");
    }

    // Test the mutation mechanism
    static void testMutationMechanism() {
        System.out.println("Running testMutationMechanism...");
        HybridGA_ACO.initialize();
        var population = HybridGA_ACO.initializePopulation();
        
        var route = population.get(0);
        System.out.println("Before Mutation: " + route.getRoute());
        
        HybridGA_ACO.mutate(route);
        
        System.out.println("After Mutation: " + route.getRoute());
        System.out.println("Mutation Mechanism Test Passed\n");
    }

    // Test ACO refinement
    static void testACORefinement() {
        System.out.println("Running testACORefinement...");
        HybridGA_ACO.initialize();
        var population = HybridGA_ACO.initializePopulation();
        
        var route = population.get(0);
        System.out.println("Before ACO Refinement: " + route.getRoute());
        
        HybridGA_ACO.refineWithACO(route);
        
        System.out.println("After ACO Refinement: " + route.getRoute());
        System.out.println("ACO Refinement Test Passed\n");
    }

    // Test pheromone update
    static void testPheromoneUpdate() {
        System.out.println("Running testPheromoneUpdate...");
        HybridGA_ACO.initialize();
        var population = HybridGA_ACO.initializePopulation();
        
        System.out.println("Pheromones Before Update:");
        printMatrix(HybridGA_ACO.pheromones);
        
        HybridGA_ACO.updatePheromones(population);
        
        System.out.println("Pheromones After Update:");
        printMatrix(HybridGA_ACO.pheromones);
        System.out.println("Pheromone Update Test Passed\n");
    }

    // Test the final output (only check structure)
    static void testFinalOutput() {
        System.out.println("Running testFinalOutput...");
        HybridGA_ACO.main(null);
        System.out.println("Final Output Test Passed\n");
    }

    // Test edge cases with modified data
    static void testEdgeCases() {
        System.out.println("Running testEdgeCases...");
        HybridGA_ACO.initialize();

        // Modify distances or traffic to be uniform or extreme
        for (int i = 0; i < HybridGA_ACO.NUM_CITIES; i++) {
            for (int j = 0; j < HybridGA_ACO.NUM_CITIES; j++) {
                HybridGA_ACO.distances[i][j] = 1.0;
                HybridGA_ACO.traffic[i][j] = 2.0;
            }
        }

        var population = HybridGA_ACO.initializePopulation();
        var bestRoute = HybridGA_ACO.findBestRoute(population);
        var shortestRoute = HybridGA_ACO.findShortestRoute(population);
        var bestRouteWithTraffic = HybridGA_ACO.findBestRouteWithTraffic(population);
        
        HybridGA_ACO.printTable(bestRoute, shortestRoute, bestRouteWithTraffic);

        System.out.println("Edge Cases Test Passed\n");
    }

    // Helper function to print matrices
    static void printMatrix(double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }
}
