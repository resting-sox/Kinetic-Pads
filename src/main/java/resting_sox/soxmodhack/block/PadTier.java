package resting_sox.soxmodhack.block;

public enum PadTier {

    SOFT ("kinetic_pad_soft",   0.6,    60,     5),
    NORMAL ("kinetic_pad,"  ,   1.2,    100,    8),
    HARD ("kinetic_pad_hard",   2.0,    160,    14);

    private final String id;
    private final double launchPower;
    private final int protectionTicks;
    private final int particleCount;

    PadTier(String id, double launchPower, int protectionTicks, int particleCount){
        this.id = id;
        this.launchPower = launchPower;
        this.protectionTicks = protectionTicks;
        this.particleCount = particleCount;
    }

    public String id(){return id;}
    public double launchPower(){return launchPower;}
    public int protectionTicks(){return protectionTicks;}
    public int particleCount(){return particleCount;}
}
