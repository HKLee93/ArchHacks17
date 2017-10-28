#include <Wire.h>
#include <SparkFun_MMA8452Q.h>
MMA8452Q accel;
unsigned long counter = 0;
unsigned long deltaTime = 1;
unsigned int cccc = 0;
float xAccel = 0;
float yAccel = 0;
float zAccel = 0;

void setup(){
  Serial.begin(9600);
  accel.init();
  analogReference(INTERNAL);
}

void loop(){
    unsigned long timestamp = millis();
    if((timestamp-counter)>deltaTime){
        sendAccel(timestamp,cccc);
        cccc = cccc+1;
        counter = counter + deltaTime;
        if(cccc>=10){
          cccc=0;
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

void sendAccel(unsigned long timestamp, unsigned int cccc){
   if(accel.available()){
    accel.read();
    xAccel += accel.cx;
    yAccel += accel.cy;
    zAccel += accel.cz;
    if(cccc==9){
      float totalX=xAccel/10;
      float totalY=yAccel/10;
      float totalZ=zAccel/10;
      Serial.write("!");
      Serial.write("0");
      Serial.write(timestamp >> 24);
      Serial.write(timestamp >> 16);
      Serial.write(timestamp >> 8);
      Serial.write(timestamp);
      unsigned long rawX = *(unsigned long *) & totalX;
      Serial.write(rawX>>24);
      Serial.write(rawX>>16);
      Serial.write(rawX>>8);
      Serial.write(rawX);
      Serial.write(",");
      unsigned long rawY = *(unsigned long *) & totalY;
      Serial.write(rawY>>24);
      Serial.write(rawY>>16);
      Serial.write(rawY>>8);
      Serial.write(rawY);
      Serial.write(",");
      unsigned long rawZ = *(unsigned long *) & totalZ;
      Serial.write(rawZ>>24);
      Serial.write(rawZ>>16);
      Serial.write(rawZ>>8);
      Serial.write(rawZ);
      xAccel = 0;
      yAccel = 0;
      zAccel = 0;
    }
  }  
}
