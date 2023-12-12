# detects both Cone and Cube
import cv2
import numpy as np

# global variables go here:
testVar = 0

# To change a global variable inside a function,
# re-declare it with the 'global' keyword
def incrementTestVar():
    global testVar
    testVar = testVar + 1
    if testVar == 100:
        print("test")
    if testVar >= 200:
        print("print")
        testVar = 0


    
# runPipeline() is called every frame by Limelight's backend.
def runPipeline(image, llrobot):
    # convert the input image to the HSV color space
    img_hsv = cv2.cvtColor(image, cv2.COLOR_BGR2HSV)
    # convert the hsv to a binary image by removing any pixels
    # that do not fall within the following HSV Min/Max values
    yellow_low = np.array([17,235,181]) # [15,229,132]
    yellow_high = np.array([27,255,245]) # [29,255,255]
    purple_low = np.array([113,82,77])
    purple_high = np.array([139,216,200])
    img_thresholdy = cv2.inRange(img_hsv, yellow_low, yellow_high)
    img_thresholdp = cv2.inRange(img_hsv, purple_low, purple_high)
    # find contours in the new binary image
    contoursy, _ = cv2.findContours(img_thresholdy,
    cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    contoursp, _ = cv2.findContours(img_thresholdp,
                                    cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    largestContour = np.array([[]])
    # initialize an empty array of values to send back to the robot
    llpython = [0,0,0,0,0,0,0,0]
    # if contours have been detected, draw them
    if (len(contoursy) > 0) or (len(contoursp)>0):
        cv2.drawContours(image, contoursy, -1, (224, 227, 39), 2)
        cv2.drawContours(image,contoursp, -1, (64, 39, 227), 2)
        contours = contoursy + contoursp
        # record the largest contour
        largestContour = max(contours, key=cv2.contourArea)
        if (len(contoursy)>0):
            largestContoury = max(contoursy, key=cv2.contourArea) 
            if np.array_equal(largestContour,largestContoury):
                c = 1.0 # true for cone
                print("Cone")
        if (len(contoursp)>0):
            largestContourp = max(contoursp, key=cv2.contourArea)
            if np.array_equal(largestContour,largestContourp) :
                c = 0.0 # false for cube
                # print("Cube") for debugging purposes 
        

            

            
        else:
            c = 2
        # get the unrotated bounding box that surrounds the contour
        x,y,w,h = cv2.boundingRect(largestContour)
        # draw the unrotated bounding box
        cv2.rectangle(image,(x,y),(x+w,y+h),(0,255,255),2)
        # record some color data 
        llpython = [c,x,y,w,h,9,8,7]
        
  
    # make sure to return a contour,
    # an image to stream,
    # and optionally an array of up to 8 values for the "llpython"
    # networktables array
    return largestContour, image, llpython
    