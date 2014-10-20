//
//  iPodCommand.h
//  HelloPhoneGap
//
//  Created by Hiedi Utley on 4/1/11.
//  Copyright 2011 Chariot Solutions, LLC. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <Cordova/CDV.h>
#import <MediaPlayer/MediaPlayer.h>

@interface MediaLibrary : CDVPlugin <MPMediaPickerControllerDelegate, UITableViewDelegate> {
        
    
}

@property (nonatomic, retain) MPMusicPlayerController * player;

- (void) showMediaPicker:(CDVInvokedUrlCommand *)command;

@end
