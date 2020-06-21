# PinEntryEditText in Kotlin for Android Apps
A Custom Pin entry EditText View, which extends AppCompatEditText
This custom view is purely written in Kotlin language.

This class PinEntryEditText extends AppCompatEditText and draws boxes for user to enter pin code.
 You can modify following attributes in the XML for this PinEntryEditText:
  1. mNumChars : The number of chars in the passcode
  2. mIsMask : true, if the mask is required; false otherwise
  3. mMaskChar: The mask character if, mask is required
  4. mBorderColor : The color of the rectangle border for each box
  5. mTextColor: The color of the text for pin
 
  Masking:-
       The characters are masked as follows: When you type and move to the next character, the previous
       character is masked. Eg.: 12345 will be displayed as ****5

Demo Video : https://github.com/karan4c6/PinEntryEditText/blob/master/extras/CustomPinEntryEditText_Demo.mp4

![Sample](/extras/demo.png)

