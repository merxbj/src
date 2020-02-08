LIST p=18F87J11
#include <P18F87J11.INC>

CONFIG	FOSC = HS
CONFIG	WDTEN = OFF
CONFIG	XINST = OFF

CBLOCK	0x000
WREG_TEMP
STATUS_TEMP
BSR_TEMP
ENDC

#define K0 0xc
#define K1 0x2
#define K2 0xf

#define RESET_BUTTON_PRESSED	0 ; indicates whether the button IS pressed
#define PAUSE_BUTTON_PRESSED	1 
#define RESET_BUTTON_PRESS_HANDLED 2 ; indicates whether the button press WAS handled
#define PAUSE_BUTTON_PRESS_HANDLED 3

#define DEBOUNCE_COUNT 3

CBLOCK
RESULT:3		; 3 blocks, ie. 24b for result
TEMP_RESULT:3	; temp calculations
COUNTER
PAUSE_BUTTON_COUNT
RESET_BUTTON_COUNT
BUTTON_STATUS
FAKE_PORTB
FAKE_PORTA
ENDC

ORG 0x0000
goto Main

ORG 0x0008
goto HighInt

ORG 0x0018
goto LowInt

HighInt:

	push STATUS
	push WREG
	push BSR
	
	call ResetTimer0
	BCF INTCON, 2, 0	; clear the int flag

	; light the LEDs
	movff COUNTER, PORTD

	call CalculatePolynom;

	; load increment the counter (please observe that this is not an atomic operation)
	movf COUNTER, WREG
	addlw 1
	movwf COUNTER

	pop BSR
	pop WREG
	pop STATUS
	retfie

LowInt:

	push STATUS
	push WREG
	push BSR
	
	call ResetTimer1
	BCF PIR1, 0, 0	; clear the int flag

	call UpdateButtonsStatus
	call HandleButtons

	pop BSR
	pop WREG
	pop STATUS
	retfie

CalculatePolynom:
	
	; clear the result
	clrf RESULT
	clrf RESULT+1
	clrf RESULT+2

	; calculate the K2x^2 and add it to the result
	movff COUNTER, WREG
	mulwf COUNTER
	movff PRODL, RESULT
	movff PRODH, RESULT+1
	movlw K2
	mulwf RESULT
	movff PRODL, TEMP_RESULT
	movff PRODH, TEMP_RESULT+1
	mulwf RESULT+1
	movff PRODL, WREG
	addwf TEMP_RESULT+1
	movff PRODH, WREG
	addwfc TEMP_RESULT+2, 1, 0
	movff TEMP_RESULT, RESULT
	movff TEMP_RESULT+1, RESULT+1
	movff TEMP_RESULT+2, RESULT+2

	; calculate the K1x and add it to the result
	movff COUNTER, WREG
	mullw K1
	movff PRODL, WREG
	addwf RESULT
	movff PRODH, WREG
	addwfc RESULT+1, 1, 0
	movlw 0
	addwfc RESULT+2, 1, 0

	; add the K0 to the result
	movlw K0
	addwf RESULT
	movlw 0
	addwfc RESULT+1
	addwfc RESULT+2

	return

UpdateButtonsStatus:
	
	; // c++ pseudocode of debounce rutine
	; if (btnStatus != actualStatus) {
	;     if (btnCount-- == 0) {
	;	      btnStatus = actualStatus;
    ;         return;
    ;     }
	; } else {
	;     btnCount = DEBOUNCE_COUNT; // start over
	; }
	;

	; is the PAUSE button now indicated to be pressed or released?
	btfsc FAKE_PORTB, RB0
	bra PauseButtonMaybePressed
	bra PauseButtonMaybeReleased

PauseButtonMaybePressed:

	; pause button is now indicated to be pressed
	; let's wait whether it persist for three consecutive interupts (10ms each)
	btfsc BUTTON_STATUS, PAUSE_BUTTON_PRESSED
	bra PauseButtonMaybeBounced
	movlw 1
	subwf PAUSE_BUTTON_COUNT
	bnz PauseButtonStatusExit
	bsf BUTTON_STATUS, PAUSE_BUTTON_PRESSED
	bcf BUTTON_STATUS, PAUSE_BUTTON_PRESS_HANDLED
	bra PauseButtonStatusExit

PauseButtonMaybeReleased:

	; pause button is now indicated to be released
	; let's wait whether it persist for three consecutive interupts (10ms each)
	btfss BUTTON_STATUS, PAUSE_BUTTON_PRESSED
	bra PauseButtonMaybeBounced
	movlw 1
	subwf PAUSE_BUTTON_COUNT
	bnz PauseButtonStatusExit
	bcf BUTTON_STATUS, PAUSE_BUTTON_PRESSED
	bra PauseButtonStatusExit

PauseButtonMaybeBounced:

	; pause button did not changed its actual state
	; this might mean nothing or it maybe bounced
	; either way, three consecutive interupts did 
	; not occured and therefore reset
	movlw DEBOUNCE_COUNT
	movwf PAUSE_BUTTON_COUNT
	bra PauseButtonStatusExit

PauseButtonStatusExit:

	; is the RESET button now indicated to be pressed or released?
	btfsc FAKE_PORTA, RA5
	bra ResetButtonMaybePressed
	bra ResetButtonMaybeReleased

ResetButtonMaybePressed:

	; pause button is now indicated to be pressed
	; let's wait whether it persist for three consecutive interupts (10ms each)
	btfsc BUTTON_STATUS, RESET_BUTTON_PRESSED
	bra ResetButtonMaybeBounced
	movlw 1
	subwf RESET_BUTTON_COUNT
	bnz ResetButtonStatusExit
	bsf BUTTON_STATUS, RESET_BUTTON_PRESSED
	bcf BUTTON_STATUS, RESET_BUTTON_PRESS_HANDLED
	bra ResetButtonStatusExit

ResetButtonMaybeReleased:

	; pause button is now indicated to be released
	; let's wait whether it persist for three consecutive interupts (10ms each)
	btfss BUTTON_STATUS, RESET_BUTTON_PRESSED
	bra ResetButtonMaybeBounced
	movlw 1
	subwf RESET_BUTTON_COUNT
	bnz ResetButtonStatusExit
	bcf BUTTON_STATUS, RESET_BUTTON_PRESSED
	bra ResetButtonStatusExit

ResetButtonMaybeBounced:
	
	; pause button did not changed its actual state
	; this might mean nothing or it maybe bounced
	; either way, three consecutive interupts did 
	; not occured and therefore reset
	movlw DEBOUNCE_COUNT
	movwf RESET_BUTTON_COUNT
	bra ResetButtonStatusExit

ResetButtonStatusExit:

	return

HandleButtons:

HandleResetButton:

	; if reset button is pressed and has not been handled yet
	btfss BUTTON_STATUS, RESET_BUTTON_PRESSED
	bra HandlePauseButton
	btfsc BUTTON_STATUS, RESET_BUTTON_PRESS_HANDLED
	bra HandlePauseButton
	; reset the counter
	clrf COUNTER
	bsf BUTTON_STATUS, RESET_BUTTON_PRESS_HANDLED
	bra HandlePauseButton

HandlePauseButton:

	; if pause button is pressed and has not been handled yet
	btfss BUTTON_STATUS, PAUSE_BUTTON_PRESSED
	bra HandleButtonsExit
	btfsc BUTTON_STATUS, PAUSE_BUTTON_PRESS_HANDLED
	bra HandleButtonsExit
	; toggle the timer 0 on/off	
	btg T0CON, 7
	bsf BUTTON_STATUS, PAUSE_BUTTON_PRESS_HANDLED
	bra HandleButtonsExit

HandleButtonsExit:

	return

Main:

	; clear all the ports
	clrf PORTA
	clrf PORTB	
	clrf PORTD

	; configure all the ports
	movlw 20h	; input button on bit 5
	movwf TRISA
	movlw 1h	; input button on bit 1
	movwf TRISB
	clrf TRISD	; all output - diodes

	; configure button handling
	movlw DEBOUNCE_COUNT
	movwf PAUSE_BUTTON_COUNT
	movwf RESET_BUTTON_COUNT
	clrf BUTTON_STATUS

	; clear the counter
	clrf COUNTER

	; enable the priority level int system
	movlw 80h
	movwf RCON

	; configure the interupt system
	movlw 0E0h
	movwf INTCON
	
	; tmr0 is high priority
	movlw 04h
	movwf INTCON2

	; tmr1 is low priority
	movlw 00h
	movwf IPR1

	; enable tmr1 overflow interupt
	movlw 01h
	movwf PIE1

	; configure the timer 0
	;movlw b'00000101'
	movlw b'00000000'
	movwf T0CON

	; configure the timer 1
	movlw b'10110100'
	movwf T1CON

	call ResetTimer0
	call ResetTimer1

	; enable the timer 0 and 1
	BSF T0CON, 7, 0
	BSF T1CON, 0, 0

Loop:
	goto Loop ; this is just a sad story of this program :-(

ResetTimer0:

	; configure the timer delay (1300ms, prescaler = 64)
	movlw 039h
	movwf TMR0H	; buffered, will be written as soon as TMR0L is written
	movlw 0A3h
	movwf TMR0L
	return

ResetTimer1:

	; configure the timer delay (1300ms, prescaler = 64)
	movlw 0F3h
	movwf TMR1H	; buffered, will be written as soon as TMR0L is written
	movlw 0CBh
	movwf TMR1L
	return

END