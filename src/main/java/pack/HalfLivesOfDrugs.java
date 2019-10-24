package pack;

import lombok.Getter;
import lombok.Setter;

class HalfLivesOfDrugs {

    HalfLivesOfDrugs(String drugName, Integer oneDose, Double halfLife, Double maxConcentration) {
        this.drugName = drugName;
        this.oneDose = oneDose;
        this.halfLife = halfLife;
        this.maxConcentration = maxConcentration;
    }


    @Getter
    @Setter
    private String drugName;


    @Getter
    @Setter
    private Integer oneDose;

    @Getter
    @Setter
    private Double halfLife;

    @Getter
    @Setter
    private Double maxConcentration;


}
