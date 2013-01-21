//
//  MyPlaylistViewController.h
//  Banden Lehia
//
//  Created by Iker Mendilibar on 24/10/12.
//  Copyright (c) 2012 Irontec S.L. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <QuartzCore/QuartzCore.h>
#import "GAITrackedViewController.h"

@interface MyPlaylistViewController : GAITrackedViewController
@property(weak, nonatomic) IBOutlet UITableView *tableView;
@property(nonatomic) NSArray *playlistArray;
-(IBAction)help:(id)sender;
-(void)alertCancel;
@end
