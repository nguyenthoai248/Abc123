package com.airdroid.tools.devices;

import java.util.HashMap;

public interface DevicesInterface {
    // Dua ra thong tin cua tung thiet bi nhu: CPU, RAM, ROM,...
    public HashMap<String, Double> getInfo();
}