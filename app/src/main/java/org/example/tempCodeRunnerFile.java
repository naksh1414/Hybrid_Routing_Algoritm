import java.util.*;

public class HybridGA_ACO {

    static final int NUM_CITIES = 10;
    static final int POPULATION_SIZE = 90;
    static final int MAX_GENERATIONS = 100;
    static final double MUTATION_RATE = 0.1;
    static final double EVAPORATION_RATE = 0.5;
    static final double ALPHA = 1.0;  // Pheromone importance
    static final double BETA = 5.0;   // Distance importance
    static final double Q = 100.0;    // Pheromone contribution

    static Random random = new Random();
    static double[][] distances = new double[NUM_CITIES][NUM_CITIES];
    static double[][] traffic = new double[NUM_CITIES][NUM_CITIES];
    static double[][] pheromones = new double[NUM_CITIES][NUM_CITIES];

    public static void main(String[] args) {
        initialize();

        List<Route> population = initializePopulation();

        Route bestRoute = null;
        Route shortestRoute = null;
        Route bestRouteWithTraffic = null;

        for (int generation = 0; generation < MAX_GENERATIONS; generation++) {
            List<Route> newPopulation = new ArrayList<>();

            for (int i = 0; i < POPULATION_SIZE; i++) {
                Route parent1 = selectParent(population);
                Route parent2 = selectParent(population);

                Route child = crossover(parent1, parent2);
                mutate(child);
                refineWithACO(child);

                newPopulation.add(child);
            }

            population = newPopulation;
            updatePheromones(population);

            Route currentBestRoute = findBestRoute(population);
            Route currentShortestRoute = findShortestRoute(population);
            Route currentBestRouteWithTraffic = findBestRouteWithTraffic(population);

            if (bestRoute == null || currentBestRoute.getDistance() < bestRoute.getDistance()) {
                bestRoute = currentBestRoute;
            }

            if (shortestRoute == null || currentShortestRoute.getDistanceWithoutTraffic() < shortestRoute.getDistanceWithoutTraffic()) {
                shortestRoute = currentShortestRoute;
            }

            if (bestRouteWithTraffic == null || currentBestRouteWithTraffic.getDistance() < bestRouteWithTraffic.getDistance()) {
                bestRouteWithTraffic = currentBestRouteWithTraffic;
            }

            System.out.println("Generation " + generation + ":");
            printTable(bestRoute, shortestRoute, bestRouteWithTraffic);
        }

        System.out.println("-----------------------------------------------------------------------");

        System.out.println("Final Results:");
        printTable(bestRoute , shortestRoute , bestRouteWithTraffic);
    }

    static void initialize() {
        // Initialize distances
        double[][] realDistances = {
            {0, 10, 15, 20, 25, 30, 35, 40, 45, 50},
            {10, 0, 35, 25, 30, 20, 15, 10, 30, 25},
            {15, 35, 0, 30, 20, 25, 30, 35, 40, 45},
            {20, 25, 30, 0, 15, 10, 20, 30, 35, 40},
            {25, 30, 20, 15, 0, 10, 15, 20, 30, 35},
            {30, 20, 25, 10, 10, 0, 20, 25, 30, 35},
            {35, 15, 30, 20, 15, 20, 0, 15, 25, 30},
            {40, 10, 35, 30, 20, 25, 15, 0, 15, 20},
            {45, 30, 40, 35, 30, 30, 25, 15, 0, 15},
            {50, 25, 45, 40, 35, 35, 30, 20, 15, 0}
        };

        // Initialize traffic levels
        double[][] realTraffic = {
            {1.0, 1.2, 1.5, 1.0, 1.3, 1.1, 1.4, 1.2, 1.3, 1.0},
            {1.2, 1.0, 1.4, 1.1, 1.3, 1.2, 1.5, 1.3, 1.4, 1.1},
            {1.5, 1.4, 1.0, 1.3, 1.2, 1.4, 1.6, 1.4, 1.5, 1.2},
            {1.0, 1.1, 1.3, 1.0, 1.1, 1.2, 1.3, 1.1, 1.2, 1.3},
            {1.3, 1.3, 1.2, 1.1, 1.0, 1.2, 1.4, 1.3, 1.2, 1.1},
            {1.1, 1.2, 1.4, 1.2, 1.2, 1.0, 1.5, 1.3, 1.4, 1.2},
            {1.4, 1.5, 1.6, 1.3, 1.4, 1.5, 1.0, 1.4, 1.5, 1.4},
            {1.2, 1.3, 1.4, 1.1, 1.3, 1.3, 1.4, 1.0, 1.2, 1.3},
            {1.3, 1.4, 1.5, 1.2, 1.2, 1.4, 1.5, 1.2, 1.0, 1.2},
            {1.0, 1.1, 1.2, 1.3, 1.1, 1.2, 1.4, 1.3, 1.2, 1.0}
        };

        for (int i = 0; i < NUM_CITIES; i++) {
            for (int j = 0; j < NUM_CITIES; j++) {
                distances[i][j] = realDistances[i][j];
                traffic[i][j] = realTraffic[i][j];
                pheromones[i][j] = 1.0; // Initial pheromone level
            }
        }
    }

    static List<Route> initializePopulation() {
        List<Route> population = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            List<Integer> route = new ArrayList<>();
            for (int j = 0; j < NUM_CITIES; j++) {
                route.add(j);
            }
            Collections.shuffle(route);
            population.add(new Route(route));
        }
        return population;
    }

    static Route selectParent(List<Route> population) {
        return population.get(random.nextInt(POPULATION_SIZE));
    }

    static Route crossover(Route parent1, Route parent2) {
        List<Integer> childRoute = new ArrayList<>(Collections.nCopies(NUM_CITIES, -1));
        int start = random.nextInt(NUM_CITIES);
        int end = random.nextInt(NUM_CITIES - start) + start;

        for (int i = start; i <= end; i++) {
            childRoute.set(i, parent1.getRoute().get(i));
        }

        int currentIndex = 0;
        for (int i = 0; i < NUM_CITIES; i++) {
            if (!childRoute.contains(parent2.getRoute().get(i))) {
                while (childRoute.get(currentIndex) != -1) {
                    currentIndex++;
                }
                childRoute.set(currentIndex, parent2.getRoute().get(i));
            }
        }

        return new Route(childRoute);
    }

    static void mutate(Route route) {
        if (random.nextDouble() < MUTATION_RATE) {
            int index1 = random.nextInt(NUM_CITIES);
            int index2 = random.nextInt(NUM_CITIES);
            Collections.swap(route.getRoute(), index1, index2);
        }
    }

    static void refineWithACO(Route route) {
        for (int i = 0; i < NUM_CITIES - 1; i++) {
            int currentCity = route.getRoute().get(i);
            int nextCity = route.getRoute().get(i + 1);

            double probability = Math.pow(pheromones[currentCity][nextCity], ALPHA) *
                    Math.pow(1.0 / (distances[currentCity][nextCity] * traffic[currentCity][nextCity]), BETA);

            if (random.nextDouble() < probability) {
                // Swap with a city that maximizes the pheromone influence
                for (int j = i + 1; j < NUM_CITIES; j++) {
                    int potentialNextCity = route.getRoute().get(j);
                    if (pheromones[currentCity][potentialNextCity] > pheromones[currentCity][nextCity]) {
                        Collections.swap(route.getRoute(), i + 1, j);
                        nextCity = potentialNextCity;
                    }
                }
            }
        }
    }

    static void updatePheromones(List<Route> population) {
        for (int i = 0; i < NUM_CITIES; i++) {
            for (int j = 0; j < NUM_CITIES; j++) {
                pheromones[i][j] *= (1 - EVAPORATION_RATE);  // Evaporation
            }
        }

        for (Route route : population) {
            double contribution = Q / route.getDistance();
            for (int i = 0; i < NUM_CITIES - 1; i++) {
                int currentCity = route.getRoute().get(i);
                int nextCity = route.getRoute().get(i + 1);
                pheromones[currentCity][nextCity] += contribution;
            }
        }
    }

    static Route findBestRoute(List<Route> population) {
        return Collections.min(population, Comparator.comparingDouble(Route::getDistance));
    }

    static Route findShortestRoute(List<Route> population) {
        return Collections.min(population, Comparator.comparingDouble(Route::getDistanceWithoutTraffic));
    }

    static Route findBestRouteWithTraffic(List<Route> population) {
        return Collections.min(population, Comparator.comparingDouble(Route::getDistance));
    }

    static void printTable(Route bestRoute, Route shortestRoute, Route bestRouteWithTraffic) {
        int columnWidth = 30;
        
        System.out.println(String.format("%-" + columnWidth + "s%-" + columnWidth + "s%-" + columnWidth + "s", "Metric", "Value", "Details"));
        
        System.out.println(String.format("%-" + columnWidth + "s%-" + columnWidth + "s%-" + columnWidth + "s", "------", "------", "-------"));
        
        System.out.println(String.format("%-" + columnWidth + "s%-" + columnWidth + ".2f%-" + columnWidth + "s", "Best Distance Route", bestRoute.getDistance(), bestRoute));
        System.out.println(String.format("%-" + columnWidth + "s%-" + columnWidth + ".2f%-" + columnWidth + "s", "Shortest Route", shortestRoute.getDistanceWithoutTraffic(), shortestRoute));
        System.out.println(String.format("%-" + columnWidth + "s%-" + columnWidth + ".2f%-" + columnWidth + "s", "Best Route with Traffic", bestRouteWithTraffic.getDistance(), bestRouteWithTraffic));
    }

    static class Route {
        private List<Integer> route;
        private double distance;
        private double[][] distances;
        private double[][] traffic;
        private double[][] pheromones;

        public Route(List<Integer> route) {
            this.route = route;
            this.distances = HybridGA_ACO.distances;
            this.traffic = HybridGA_ACO.traffic;
            this.pheromones = HybridGA_ACO.pheromones;
            this.distance = calculateDistance();
        }

        public List<Integer> getRoute() {
            return route;
        }

        public double getDistance() {
            return distance;
        }

        public double getDistanceWithoutTraffic() {
            double totalDistance = 0.0;
            for (int i = 0; i < NUM_CITIES - 1; i++) {
                totalDistance += distances[route.get(i)][route.get(i + 1)];
            }
            return totalDistance;
        }

        private double calculateDistance() {
            double totalDistance = 0.0;
            for (int i = 0; i < NUM_CITIES - 1; i++) {
                totalDistance += distances[route.get(i)][route.get(i + 1)] * traffic[route.get(i)][route.get(i + 1)];
            }
            return totalDistance;
        }

        @Override
        public String toString() {
            return route.toString();
        }
    }
}
