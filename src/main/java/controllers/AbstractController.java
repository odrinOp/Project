package controllers;

import services.ServiceMaster;

public abstract class AbstractController {

    private ServiceMaster master;

    public void initialize(ServiceMaster master){
        this.master = master;
    }


    public ServiceMaster getMaster(){
        return this.master;
    }


}
