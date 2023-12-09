# detects Cone
import cv2
import numpy as np

# global variables go here:
testVar = 0


    
# runPipeline() is called every frame by Limelight's backend.
def runPipeline(image, llrobot):
    # convert the input image to the HSV color space
    img_hsv = cv2.cvtColor(image, cv2.COLOR_BGR2HSV)
    # convert the hsv to a binary image by removing any pixels
    # that do not fall within the following HSV Min/Max values
    yellow_low = np.array([15,229,132])
    yellow_high = np.array([29,255,255])
    img_thresholdy = cv2.inRange(img_hsv, yellow_low, yellow_high)
    # find contours in the new binary image
    contoursy, _ = cv2.findContours(img_thresholdy,
    cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    largestContour = np.array([[]])
    # initialize an empty array of values to send back to the robot
    llpython = [0,0,0,0,0,0,0,0]
    # if contours have been detected, draw them
    if (len(contoursy) > 0):
        cv2.drawContours(image, contoursy, -1, (224, 227, 39), 2)
        # record the largest contour
        largestContour = max(contoursy, key=cv2.contourArea)
        # get the unrotated bounding box that surrounds the contour
        x,y,w,h = cv2.boundingRect(largestContour)
        # draw the unrotated bounding box
        cv2.rectangle(image,(x,y),(x+w,y+h),(0,255,255),2)
        # record some color data to send
        c = 1
        llpython = [c,x,y,w,h,9,8,7]
  
    # make sure to return a contour,
    # an image to stream,
    # and optionally an array of up to 8 values for the "llpython"
    # networktables array
    return largestContour, image, llpython
    