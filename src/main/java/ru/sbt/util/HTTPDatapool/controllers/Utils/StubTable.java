//package ru.sbt.util.HTTPDatapool.controllers.Utils;
//
//import ru.sbt.util.HTTPDatapool.controllers.dto.AddingStatus;
//
//import java.util.Timer;
//import java.util.TimerTask;
//
//public class StubTable {
//    AddingStatus status = AddingStatus.NEW;
//    String name;
//    Timer timer;
//
//    public AddingStatus getStatus() {
//        return status;
//    }
//    public StubTable(String name) {
//        this.name = name;
//        this.status = AddingStatus.UPDATING;
//
//        timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                status = AddingStatus.READY;
//            }
//        },5_000);
//    }
//}
