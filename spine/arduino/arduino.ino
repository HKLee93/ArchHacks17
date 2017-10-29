#include <Wire.h>
#include <SparkFun_MMA8452Q.h>
MMA8452Q accel;

unsigned int threshold = 30;
unsigned int bufferSize =10;
unsigned long counter = 0;
unsigned long deltaTime = 1;
boolean firstFlag = false;
unsigned int arbitraryC = 0;
float  xSlots[10];
float  ySlots[10];
float  zSlots[10];
float xAverage=0;
float yAverage=0;
float zAverage=0;
unsigned int cccc = 0;
float xAccel = 0;
float yAccel = 0;
float zAccel = 0;
float x = 0;
float y = 0;
float z = 0;


void setup(){
  Serial.begin(9600);
  accel.init();
  analogReference(INTERNAL);
}

void loop(){
    unsigned long timestamp = millis();
    if((timestamp-counter)>deltaTime){
        boolean check = sendAccel(timestamp);
        if(check){
          cccc = cccc+1;
          arbitraryC += 1;
          counter = counter + deltaTime;
          if(cccc>=bufferSize){
            cccc=0;
          }
        }
    }

}

void printAccel(){
  if(accel.available()){
    accel.read();
    float xAccel = accel.x;
    float yAccel = accel.y;
    float zAccel = accel.z;
    pprint(xAccel,yAccel,zAccel);
  }  
}

void pprint(int xAccel,int yAccel, int zAccel){
  int newX = (xAccel>>8)+xAccel;
  int newY = (yAccel>>8)+yAccel;
  int newZ = (zAccel>>8)+zAccel;
  Serial.print(newX);
  Serial.print(",");
  Serial.print(newY);
  Serial.print(",");
  Serial.println(newZ);
}

boolean printAccel(unsigned long timestamp){
  Serial.print("a");
   if(accel.available()){
    accel.read();
    x = accel.cx;
    y = accel.cy;
    z = accel.cz;

    if(arbitraryC>(bufferSize-1)){
      firstFlag=true;
    }

    if(arbitraryC==0){
      xAverage=x;
      yAverage=y;
      zAverage=z;
    }

    if(abs(x)>threshold*abs(xAverage)){
      return false;
    }
    else if(abs(y)>threshold*abs(yAverage)){
      return false;
    }
    

    
    if(!firstFlag){
      xSlots[arbitraryC] = x;
      ySlots[arbitraryC] = y;
      zSlots[arbitraryC] = z;
      xAverage=0.0;
      yAverage=0.0;
      zAverage=0.0;
      for(int i=0;i<=arbitraryC;++i){
        xAverage += xSlots[i];
        yAverage += ySlots[i];
        zAverage += zSlots[i];
      }
      xAverage = xAverage/(arbitraryC+1);
      yAverage = yAverage/(arbitraryC+1);
      zAverage = zAverage/(arbitraryC+1);
      arbitraryC += 1;
    }
    else{
      xSlots[cccc] = x;
      ySlots[cccc] = y;
      zSlots[cccc] = z;
      xAverage=0.0;
      yAverage=0.0;
      zAverage=0.0;
      for(int i=0;i<bufferSize;++i){
        xAverage += xSlots[i];
        yAverage += ySlots[i];
        zAverage += zSlots[i];
      }
      xAverage = xAverage/bufferSize;
      yAverage = yAverage/bufferSize;
      zAverage = zAverage/bufferSize;
    }
    if(cccc==(bufferSize-1)){
      Serial.print("!");
      Serial.print("0");
      Serial.print(timestamp);
      Serial.print(",");
      Serial.print(xAverage);
      Serial.print(",");
      Serial.print(yAverage);
      Serial.print(",");
      Serial.print(zAverage);
      Serial.print("\n");
    }
    return true;
  } 
  else{
    return false;
  }
}

boolean sendAccel(unsigned long timestamp){
   if(accel.available()){
    accel.read();
    x = accel.cx;
    y = accel.cy;
    z = accel.cz;

    if(arbitraryC>(bufferSize-1)){
      firstFlag=true;
    }

    if(arbitraryC==0){
      xAverage=x;
      yAverage=y;
      zAverage=z;
    }

    if(abs(x)>threshold*abs(xAverage)){
      return false;
    }
    else if(abs(y)>threshold*abs(yAverage)){
      return false;
    }
    else if(abs(z)>threshold*abs(zAverage)){
      return false;
    }  

    
    if(!firstFlag){
      xSlots[arbitraryC] = x;
      ySlots[arbitraryC] = y;
      zSlots[arbitraryC] = z;
      xAverage=0.0;
      yAverage=0.0;
      zAverage=0.0;
      for(int i=0;i<=arbitraryC;++i){
        xAverage += xSlots[i];
        yAverage += ySlots[i];
        zAverage += zSlots[i];
      }
      xAverage = xAverage/(arbitraryC+1);
      yAverage = yAverage/(arbitraryC+1);
      zAverage = zAverage/(arbitraryC+1);
      arbitraryC += 1;
    }
    else{
      xSlots[cccc] = x;
      ySlots[cccc] = y;
      zSlots[cccc] = z;
      xAverage=0.0;
      yAverage=0.0;
      zAverage=0.0;
      for(int i=0;i<bufferSize;++i){
        xAverage += xSlots[i];
        yAverage += ySlots[i];
        zAverage += zSlots[i];
      }
      xAverage = xAverage/bufferSize;
      yAverage = yAverage/bufferSize;
      zAverage = zAverage/bufferSize;
    }

    if(cccc==(bufferSize-1)){
      Serial.write("!");
      Serial.write("0");
      Serial.write(timestamp >> 24);
      Serial.write(timestamp >> 16);
      Serial.write(timestamp >> 8);
      Serial.write(timestamp);
      unsigned long rawX = *(unsigned long *) & xAverage;
      Serial.write(rawX>>24);
      Serial.write(rawX>>16);
      Serial.write(rawX>>8);
      Serial.write(rawX);
      Serial.write(",");
      unsigned long rawY = *(unsigned long *) & yAverage;
      Serial.write(rawY>>24);
      Serial.write(rawY>>16);
      Serial.write(rawY>>8);
      Serial.write(rawY);
      Serial.write(",");
      unsigned long rawZ = *(unsigned long *) & zAverage;
      Serial.write(rawZ>>24);
      Serial.write(rawZ>>16);
      Serial.write(rawZ>>8);
      Serial.write(rawZ);
    }
    return true;
  } 
  else{
    return false;
  }
}
